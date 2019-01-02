export const environment = {
  production: true,
  protoDirs: ['./apps/api/src/hero'],

  NODE_TLS_REJECT_UNAUTHORIZED: 0,

  server: {
    host: process.env.HOST || '0.0.0.0',
    domainUrl: process.env.DOMAIN_URL || 'http://localhost:3000',
    port: process.env.PORT || 3000,
    globalPrefix: '/api',
  },

  database: {
    type: 'postgres',
    host: process.env.TYPEORM_HOST || 'postgres',
    port: process.env.TYPEORM_PORT ? Number(process.env.TYPEORM_PORT) : 5432,
    database: process.env.TYPEORM_DATABASE || 'cockpit',
    username: process.env.TYPEORM_USERNAME || 'cockpit',
    password: process.env.TYPEORM_PASSWORD || 'cockpit123',
    keepConnectionAlive: true,
    logging: process.env.TYPEORM_LOGGING ? JSON.parse(process.env.TYPEORM_LOGGING) : false,
    synchronize: false,
  },

  auth: {
    issuer:
      process.env.OIDC_ISSUER_URL || 'https://myroute-is360.a3c1.starter-us-west-1.openshiftapps.com/auth/realms/is360',
    clientId: process.env.OIDC_CLIENT_ID || 'is360ui',
  },

  email: {
    transport: {
      host: process.env.EMAIL_HOST || 'mail.google.com',
      port: process.env.EMAIL_PORT ? Number(process.env.EMAIL_PORT) : 25,
      secure: process.env.EMAIL_SECURE ? JSON.parse(process.env.EMAIL_SECURE) : false,
      auth: {
        user: process.env.EMAIL_AUTH_USER || 'auth_user',
        pass: process.env.EMAIL_AUTH_PASS || 'auth_pass',
      },
    },
    defaults: {
      from: process.env.EMAIL_FROM ? process.env.EMAIL_FROM : '"sumo demo" <sumo@demo.com>',
    },
    templateDir: process.env.EMAIL_TEMPLATE_DIR || `${__dirname}/assets/email-templates`,
  },

  // Key generation: https://web-push-codelab.glitch.me
  webPush: {
    subject: process.env.VAPID_SUBJECT || 'mailto: sumo@demo.com',
    publicKey:
      process.env.VAPID_PUBLIC_KEY ||
      'BAJq-yHlSNjUqKW9iMY0hG96X9WdVwetUFDa5rQIGRPqOHKAL_fkKUe_gUTAKnn9IPAltqmlNO2OkJrjdQ_MXNg',
    privateKey: process.env.VAPID_PRIVATE_KEY || 'cwh2CYK5h_B_Gobnv8Ym9x61B3qFE2nTeb9BeiZbtMI',
  },
  kubernetes: {
    CLUSTER1: {
      baseUrl: 'https://cluster1:8080',
      version: '1.10',
      /* tslint:disable-next-line:max-line-length */
      token: process.env.CLUSTER1_SERVICE_ACCOUNT_TOKEN || 'eyJhbGciOiJSUzI1NiIsImtpZCI6IiJ9.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJkZWZhdWx0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6ImFkbWluLXVzZXItdG9rZW4tOThndDUiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC5uYW1lIjoiYWRtaW4tdXNlciIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50LnVpZCI6ImY0ZjM3MjhiLTdiMGMtMTFlOC05YTIzLWE4MWU4NDdkOGY3YyIsInN1YiI6InN5c3RlbTpzZXJ2aWNlYWNjb3VudDpkZWZhdWx0OmFkbWluLXVzZXIifQ.IodSUAaRt8UmuL4eEhUq_ciJf182I3mxl0gHyY3nuFJ773pAnOVu_BZopQXYcWgHg8Q2b3dU1Vh2uacec4ZScZENNJw2ytFVAfZCWrhWg7Pj9s2hlKIWwdp7HcDpI8T3mF99_ykSfVBaVINlFhn6jNVMOc3jG1fPLZ7BEyO30ZrqEFW-BgRRlq-DKnfqLDPcYQeC7xNIYp4x52X1bYKC6B8k07VCpzRPr59lX0ymCXjkFQ7js7tTosrFanXByPGW91Mjregfa6RdazhR1dNL1FAbl_cEts2O9vKvP8wGYoYPbmsuMvuqrNH9t4QA7Q5e1GLMwN-1ZXun7BrufwTGFA',
    },
    CLUSTER2: {
      baseUrl: 'https://cluster2:8080',
      version: '1.10',
      /* tslint:disable-next-line:max-line-length */
      token: process.env.CLUSTER2_SERVICE_ACCOUNT_TOKEN || 'eyJhbGciOiJSUzI1NiIsImtpZCI6IiJ9.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJkZWZhdWx0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6ImFkbWluLXVzZXItdG9rZW4teGw4OGsiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC5uYW1lIjoiYWRtaW4tdXNlciIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50LnVpZCI6IjkzYzJmMjdhLTkxYzItMTFlOC04YmFkLWE4MWU4NGE1MDM2MiIsInN1YiI6InN5c3RlbTpzZXJ2aWNlYWNjb3VudDpkZWZhdWx0OmFkbWluLXVzZXIifQ.vgx-a8vPX7jeIHQj_2fkTRNmwn8tcSAbCoxqVoJjM4NQXcC_N5xUNuM3CgMdgjvIh3R_EcyIE0TgGHacZG6XdmeNZm5bSybHkuFOXO41sHvpPhvwK3tP7x_1iVN3SWZiGvXWNOfbhjKnIJWteKNvn1P8wNGC7gcAIaBw6AyxhKKzhtelBMWB3Qu_Ka29nuuUUdOjqv4G8GiuR9lMCrZNoSRu05iS2HlKVup9pWEjODl1ED7ZAThBljxlr3oedA34gEvtnVgkKtPkD_R2H1zJTLm7iB5f_TjQRjvYaQcEUZH7pipan3jHPbmwFZ-PDyJYZMTm9Wlqc4fiH3V0edJ_3w',
    },
    CLUSTER3: {
      baseUrl: 'https://cluster3:8080',
      version: '1.10',
      /* tslint:disable-next-line:max-line-length */
      token: process.env.CLUSTER3_SERVICE_ACCOUNT_TOKEN || 'eyJhbGciOiJSUzI1NiIsImtpZCI6IiJ9.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJkZWZhdWx0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6ImFkbWluLXVzZXItdG9rZW4tNHA4ZHMiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC5uYW1lIjoiYWRtaW4tdXNlciIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50LnVpZCI6ImQ0MTRmMzFlLWFhYzAtMTFlOC05MmYxLWVjZWJiODk4YTA5NCIsInN1YiI6InN5c3RlbTpzZXJ2aWNlYWNjb3VudDpkZWZhdWx0OmFkbWluLXVzZXIifQ.MTVZ1fA8kSeAGFF9wYkbEyrgOdT7nM5nw-imniUZdguMvjaNbZxcf8-lt1AaiZJLLv1XHXjhSIbzmDEH1-hFMPrZ4CO5injcCBjgR_VuAU_OVGdEH3v-eN1PHKHMm3KBPgJh7jV_6Be9NvX9rKQtRTEGpzzDukdd3Z0j3el5DEgyB-eTwpXXyTYVD7lm5aPM3-iMJTsvYIvx34pD_D47cRmKqrf2M9xzrrOqjPvkiRRDhV34htPFVHXKUdb35H1aelk-3u7PS9jjtzZJBdOvPGkid7ch_GBp_faPj_gTa-x0a_JTBgdE4WjADSEVct0xEcZcZQqR5L_XjdG1Lef1rA',
    },
    CLUSTER4: {
      baseUrl: 'https://cluster4:8080',
      version: '1.10',
      /* tslint:disable-next-line:max-line-length */
      token: process.env.CLUSTER4_SERVICE_ACCOUNT_TOKEN || 'eyJhbGciOiJSUzI1NiIsImtpZCI6IiJ9.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJkZWZhdWx0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6ImFkbWluLXVzZXItdG9rZW4tNWR6bjQiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC5uYW1lIjoiYWRtaW4tdXNlciIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50LnVpZCI6IjdhNDRjMjY5LWNiMjQtMTFlOC04ZDc3LWQwYmY5Y2I3MWMyOCIsInN1YiI6InN5c3RlbTpzZXJ2aWNlYWNjb3VudDpkZWZhdWx0OmFkbWluLXVzZXIifQ.G6Z-M2b54m22BoThSuweu3k7KZ2lnNcQvNP5pu92xAZ_hPoQFidiPgcraxAlRVgGkfQaqnVI1iQFJYAXXap3P_kURH9ee4akYGxoK7Hc7eklWPA6zobilOHBz-aCS7dlDoiQZ4GMKjFi-q1DLzZxecu9NBOO9UUku2a6d76OqIA7NBBcOQDCBCpt0EXtkq9ZLc2wcod464Kv3xBb2J8PLWavEI58-oBeuXWHo4aFGU2VzT7BYIAVLpZp6WY7LfsOtBYG-0HkjZ-GmXOEMSsA5zdU__svAuEcH2kg_ngtfKkHdfTjUcR_eqfQu2bhfb_ZrJnOnt__pw2PYRqo1uQoPw',
    },
  },
};
