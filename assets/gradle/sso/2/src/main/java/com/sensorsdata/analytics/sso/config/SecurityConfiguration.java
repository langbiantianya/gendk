package com.sensorsdata.analytics.sso.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.saml2.core.Saml2X509Credential;
import org.springframework.security.saml2.provider.service.registration.InMemoryRelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrations;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.HashSet;

import static org.springframework.security.config.Customizer.withDefaults;


@Slf4j
@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${saml2.metadata-url}")
    private String metadataUrl;

    @Value("${saml2.entityId}")
    private String entityId;

    @Value("${saml2.hostname}")
    private String hostname;

    @Value("${saml2.storepass}")
    private String storepass;

    @Value("${saml2.keypass}")
    private String keypass;

    @Value("${saml2.alias}")
    private String alias;

    @Value("${saml2.storeFilePath}")
    private String jksPath;

    @Override
    protected void configure(final HttpSecurity http) throws Exception {


        http.csrf().ignoringAntMatchers("/userinfo*").and()
                .authorizeRequests()
                .antMatchers("/saml*", "/saml2/**", "/userinfo*", "/saml/sp/metadata*", "/login/**", "/idp_metadata.xml").permitAll()
                .anyRequest().authenticated()
                .and()
                .saml2Login(withDefaults());
    }


    @Bean()
    public RelyingPartyRegistrationRepository relyingPartyRegistrationRepository()
            throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException {
        String domain = "https://" + hostname;
        String registrationId = "更换为项目实际地址";
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(new ClassPathResource(jksPath).getInputStream(), storepass.toCharArray());
        log.info("JKS file loaded successfully");
        X509Certificate certificate = (X509Certificate) keyStore.getCertificate(alias);
        log.info("Certificate loaded successfully");
        PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, keypass.toCharArray());
        log.info("Private key loaded successfully");
        // 确保签名证书配置
        Saml2X509Credential signingCredential = new Saml2X509Credential(privateKey,
                certificate,
                new HashSet<>(Collections.singletonList(Saml2X509Credential.Saml2X509CredentialType.SIGNING)));
        // 加密证书
        Saml2X509Credential decryptCredential = new Saml2X509Credential(privateKey,
                certificate,
                new HashSet<>(Collections.singletonList(Saml2X509Credential.Saml2X509CredentialType.DECRYPTION)));
        // 配置 RelyingPartyRegistration 并加入签名证书
        RelyingPartyRegistration registration = RelyingPartyRegistrations
                .fromMetadataLocation(metadataUrl)
                .entityId(entityId)
                .registrationId(registrationId)
                .assertionConsumerServiceLocation(domain + "/login/saml2/sso/" + registrationId)
                .signingX509Credentials(c -> c.add(signingCredential))  // 确保签名凭证已添加
                .decryptionX509Credentials(d -> d.add(decryptCredential))
                .assertingPartyDetails(p -> p
                        .wantAuthnRequestsSigned(false)  // 启用请求签名
                )
                .build();


        return new InMemoryRelyingPartyRegistrationRepository(registration);
    }


}
