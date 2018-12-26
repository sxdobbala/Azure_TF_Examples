#!/bin/bash
#
# Run as root - installs apache and configures basic reverse proxy for splunk on port 80/443 (using built-in cert)

APTGET="$$(which apt-get 2>/dev/null)"
YUM="$$(which yum 2>/dev/null)"

if [ -e "$$APTGET" ]; then
  echo "Installing apache and related packages..."
  apt-get install -y apache2
  a2dismod -f auth_basic status dir authn_core authn_file autoindex setenvif negotiation
  a2enmod -f headers proxy proxy_http ssl rewrite
  a2disconf localized-error-pages other-vhosts-access-log serve-cgi-bin charset
  a2ensite default-ssl

  # LetsEncrypt stuff
  add-apt-repository -y ppa:certbot/certbot
  apt-get -y install python-certbot-apache

  cat >/etc/apache2/apache2.conf <<'EOM'
Mutex file:$${APACHE_LOCK_DIR} default
PidFile $${APACHE_PID_FILE}
Timeout 300
KeepAlive On
MaxKeepAliveRequests 100
KeepAliveTimeout 5
User $${APACHE_RUN_USER}
Group $${APACHE_RUN_GROUP}
HostnameLookups Off
ErrorLog $${APACHE_LOG_DIR}/error.log
LogLevel warn
ServerName ${fqdn}
IncludeOptional mods-enabled/*.load
IncludeOptional mods-enabled/*.conf
Include ports.conf
<Directory />
  Options FollowSymLinks
  AllowOverride None
  Require all denied
</Directory>
<FilesMatch "^\.ht">
  Require all denied
</FilesMatch>
LogFormat "%v:%p %h %l %u %t \"%r\" %>s %O \"%{Referer}i\" \"%{User-Agent}i\"" vhost_combined
LogFormat "%h %l %u %t \"%r\" %>s %O \"%{Referer}i\" \"%{User-Agent}i\"" combined
LogFormat "%h %l %u %t \"%r\" %>s %O" common
LogFormat "%{Referer}i -> %U" referer
LogFormat "%{User-agent}i" agent
IncludeOptional conf-enabled/*.conf
IncludeOptional sites-enabled/*.conf
ProxyRequests Off
ProxyPass / http://localhost:8000/
ProxyPassReverse / http://localhost:8000/
EOM

  cat >/etc/apache2/conf-enabled/security.conf <<'EOM'
ServerTokens Prod
ServerSignature Off
TraceEnable Off
Header set X-Frame-Options: "sameorigin"
EOM

  cat >/etc/apache2/sites-enabled/000-default.conf <<'EOM'
<VirtualHost *:80>
        ServerAdmin webmaster@localhost
        DocumentRoot /var/www/html
        ErrorLog $${APACHE_LOG_DIR}/error.log
        CustomLog $${APACHE_LOG_DIR}/access.log combined
        RewriteEngine On
        RewriteRule ^ https://%{SERVER_NAME}%{REQUEST_URI} [END,QSA,R=permanent]
</VirtualHost>
EOM

  echo "Getting LetsEncrypt Cert..."

  certbot --apache -n --agree-tos --email 'russ.jury@optum.com' -d ${fqdn}
  systemctl enable apache2
  systemctl restart apache2

elif [ -e "$$YUM" ]; then
  echo "Installing apache and related packages..."
  yum install -y httpd mod_ssl mod_rewrite
  /usr/sbin/setsebool -P httpd_can_network_connect 1
  cat >/etc/httpd/conf/httpd.conf <<'EOM'
ServerRoot "/etc/httpd"
Listen 80
Include conf.modules.d/*.conf
User apache
Group apache
ServerAdmin root@localhost
ServerName ${fqdn}
<Directory />
    AllowOverride none
    Require all denied
</Directory>
DocumentRoot "/var/www/html"
<Files ".ht*">
    Require all denied
</Files>
LogFormat "%v:%p %h %l %u %t \"%r\" %>s %O \"%{Referer}i\" \"%{User-Agent}i\"" vhost_combined
LogFormat "%h %l %u %t \"%r\" %>s %O \"%{Referer}i\" \"%{User-Agent}i\"" combined
LogFormat "%h %l %u %t \"%r\" %>s %O" common
LogFormat "%{Referer}i -> %U" referer
LogFormat "%{User-agent}i" agent
ErrorLog "logs/error_log"
LogLevel warn
<IfModule mime_module>
    TypesConfig /etc/mime.types
<IfModule mime_magic_module>
IncludeOptional conf.d/ssl.conf
ProxyRequests Off
ProxyPass / http://localhost:8000/
ProxyPassReverse / http://localhost:8000/
ServerTokens Prod
ServerSignature Off
TraceEnable Off
Header set X-Frame-Options: "sameorigin"
EOM

# TODO - redirect HTTP to HTTPS

# TODO - set up LetsEncrypt stuff

  systemctl enable httpd
  systemctl restart httpd

else
  echo "Cannot install Apache"
  exit 255
fi

