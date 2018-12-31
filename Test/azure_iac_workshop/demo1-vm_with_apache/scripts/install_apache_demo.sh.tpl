#!/bin/bash -x
#
# Run as root - installs apache (with LetsEncrypt) and creates a hello world static page.
#
# Variables:
#   fqdn - used for ServerName and http->https redirect
#   email_address - used for LetsEncrypt (email notifications)


APTGET="$$(which apt-get 2>/dev/null)"
YUM="$$(which yum 2>/dev/null)"

if [ -e "$$APTGET" ]; then
  apt-get install -y apache2
  a2dismod -f auth_basic status authn_core authn_file autoindex setenvif negotiation
  a2enmod -f headers proxy proxy_http ssl dir
  a2disconf localized-error-pages other-vhosts-access-log serve-cgi-bin charset
  a2ensite default-ssl

  # LetsEncrypt stuff
  add-apt-repository -y ppa:certbot/certbot
  apt-get -y install python-certbot-apache

  cat >/etc/apache2/sites-available/000-default.conf <<'EOM'
<VirtualHost *:80>
  ServerAdmin webmaster@localhost
  DocumentRoot /var/www/html
  ErrorLog $${APACHE_LOG_DIR}/error.log
  CustomLog $${APACHE_LOG_DIR}/access.log combined
  RedirectMatch Permanent "^(/(?!.well-known/acme-challenge/).*)" https://${fqdn}$$1
</VirtualHost>
EOM

  cat > /etc/apache2/sites-available/000-default-le-ssl.conf <<'EOM'
<IfModule mod_ssl.c>
<VirtualHost *:443>
  ServerAdmin webmaster@localhost
  DocumentRoot /var/www/html
  ErrorLog $${APACHE_LOG_DIR}/error.log
  CustomLog $${APACHE_LOG_DIR}/access.log combined

  ServerName ${fqdn}
  SSLCertificateFile /etc/letsencrypt/live/${fqdn}/fullchain.pem
  SSLCertificateKeyFile /etc/letsencrypt/live/${fqdn}/privkey.pem
  Include /etc/letsencrypt/options-ssl-apache.conf
</VirtualHost>
</IfModule>
EOM

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
DocumentRoot /var/www/html
<Directory />
  Options FollowSymLinks
  AllowOverride None
  Require all granted
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
EOM
  cat >/etc/apache2/conf-enabled/security.conf <<'EOM'
ServerTokens Prod
ServerSignature Off
TraceEnable Off
Header set X-Frame-Options: "sameorigin"
EOM

  certbot --apache -n --agree-tos --email '${email_address}' -d ${fqdn}
  systemctl restart apache2
  systemctl enable apache2

elif [ -e "$$YUM" ]; then
  echo "NOT SUPPORTED"
  exit 100
else
  echo "Cannot install Apache"
  exit 255
fi

cat >/var/www/html/index.html <<'EOM'
<h2>Hello, World!</h2>
EOM
