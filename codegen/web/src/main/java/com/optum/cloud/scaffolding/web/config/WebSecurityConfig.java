package com.optum.cloud.scaffolding.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;

import java.util.Arrays;

/**
 * Created by akansal3 on 5/10/2017.
 */
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static String REALM = "SECURITY_REALM";
    @Value("${ldap.url}")
    private String ldap_url;
    @Value("${ldap.domain}")
    private String ldap_domain;

    @Override

    protected void configure(final HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests().antMatchers("/api/v1/healthCheck").permitAll().antMatchers("/**")
            .authenticated().and().httpBasic().realmName(REALM);
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder authManagerBuilder) throws Exception {
        authManagerBuilder.authenticationProvider(activeDirectoryLdapAuthenticationProvider()).userDetailsService(userDetailsService());
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        final ProviderManager providerManager = new ProviderManager(Arrays.asList(activeDirectoryLdapAuthenticationProvider()));
        providerManager.setEraseCredentialsAfterAuthentication(false);
        return providerManager;
    }

    @Bean
    public AuthenticationProvider activeDirectoryLdapAuthenticationProvider() {
        final ActiveDirectoryLdapAuthenticationProvider provider = new ActiveDirectoryLdapAuthenticationProvider(ldap_domain, ldap_url);
        provider.setConvertSubErrorCodesToExceptions(true);
        provider.setUseAuthenticationRequestCredentials(true);

        return provider;
    }

    /*
     * Use the below code ifworking offline and there is need to bypass ldap authentication.
     *
     * @Autowired public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception { auth
     * .inMemoryAuthentication() .withUser("user").password("password").roles("USER"); }
     */

}
