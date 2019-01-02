// This file can be replaced during build by using the `fileReplacements` array.
// `ng build ---prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,

  protoDirs: ['./apps/api/src/hero'],

  NODE_TLS_REJECT_UNAUTHORIZED: 0,
  ALLOW_WHITE_LIST: ['::ffff:127.0.0.1', '::1'],
  LOG_LEVEL: 'debug',

  server: {
    host: '0.0.0.0',
    domainUrl: 'http://localhost:3000',
    port: 3000,
    globalPrefix: '/api',
  },

  database: {
    type: 'postgres',
    host: 'localhost',
    port: 5432,
    database: 'cockpit',
    username: 'cockpit',
    password: 'cockpit123',
    keepConnectionAlive: true,
    logging: true,
    synchronize: true,
  },

  auth: {
    // issuer: 'https://myroute-is360.a3c1.starter-us-west-1.openshiftapps.com/auth/realms/kubernetes',
    // clientId: 'cockpit',
    issuer: 'https://myroute-is360.a3c1.starter-us-west-1.openshiftapps.com/auth/realms/is360',
    clientId: 'is360ui',
  },

  email: {
    transport: {
      host: 'mail.google.com',
      port: 25,
    },
    defaults: {
      from: '"sumo demo" <sumo@demo.com>',
    },
    templateDir: 'apps/api/src/assets/email-templates',
  },

  // Key generation: https://web-push-codelab.glitch.me
  webPush: {
    subject: 'mailto: sumo@demo.com',
    publicKey: 'BAJq-yHlSNjUqKW9iMY0hG96X9WdVwetUFDa5rQIGRPqOHKAL_fkKUe_gUTAKnn9IPAltqmlNO2OkJrjdQ_MXNg',
    privateKey: 'cwh2CYK5h_B_Gobnv8Ym9x61B3qFE2nTeb9BeiZbtMI',
  },
  kubernetes: {
    CTC: {
      baseUrl: 'https://k8s-prod-ctc-aci.optum.com:16443',
      version: '1.10',
      /* tslint:disable-next-line:max-line-length */
      token: 'eyJhbGciOiJSUzI1NiIsImtpZCI6IiJ9.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJkZWZhdWx0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6ImFkbWluLXVzZXItdG9rZW4tOThndDUiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC5uYW1lIjoiYWRtaW4tdXNlciIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50LnVpZCI6ImY0ZjM3MjhiLTdiMGMtMTFlOC05YTIzLWE4MWU4NDdkOGY3YyIsInN1YiI6InN5c3RlbTpzZXJ2aWNlYWNjb3VudDpkZWZhdWx0OmFkbWluLXVzZXIifQ.IodSUAaRt8UmuL4eEhUq_ciJf182I3mxl0gHyY3nuFJ773pAnOVu_BZopQXYcWgHg8Q2b3dU1Vh2uacec4ZScZENNJw2ytFVAfZCWrhWg7Pj9s2hlKIWwdp7HcDpI8T3mF99_ykSfVBaVINlFhn6jNVMOc3jG1fPLZ7BEyO30ZrqEFW-BgRRlq-DKnfqLDPcYQeC7xNIYp4x52X1bYKC6B8k07VCpzRPr59lX0ymCXjkFQ7js7tTosrFanXByPGW91Mjregfa6RdazhR1dNL1FAbl_cEts2O9vKvP8wGYoYPbmsuMvuqrNH9t4QA7Q5e1GLMwN-1ZXun7BrufwTGFA',
    },
    ELR: {
      baseUrl: 'https://k8s-prod-elr-aci.optum.com:16443',
      version: '1.10',
      /* tslint:disable-next-line:max-line-length */
      token: 'eyJhbGciOiJSUzI1NiIsImtpZCI6IiJ9.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJkZWZhdWx0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6ImFkbWluLXVzZXItdG9rZW4teGw4OGsiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC5uYW1lIjoiYWRtaW4tdXNlciIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50LnVpZCI6IjkzYzJmMjdhLTkxYzItMTFlOC04YmFkLWE4MWU4NGE1MDM2MiIsInN1YiI6InN5c3RlbTpzZXJ2aWNlYWNjb3VudDpkZWZhdWx0OmFkbWluLXVzZXIifQ.vgx-a8vPX7jeIHQj_2fkTRNmwn8tcSAbCoxqVoJjM4NQXcC_N5xUNuM3CgMdgjvIh3R_EcyIE0TgGHacZG6XdmeNZm5bSybHkuFOXO41sHvpPhvwK3tP7x_1iVN3SWZiGvXWNOfbhjKnIJWteKNvn1P8wNGC7gcAIaBw6AyxhKKzhtelBMWB3Qu_Ka29nuuUUdOjqv4G8GiuR9lMCrZNoSRu05iS2HlKVup9pWEjODl1ED7ZAThBljxlr3oedA34gEvtnVgkKtPkD_R2H1zJTLm7iB5f_TjQRjvYaQcEUZH7pipan3jHPbmwFZ-PDyJYZMTm9Wlqc4fiH3V0edJ_3w',
    },
    PTC: {
      baseUrl: 'https://k8s-prod-ptc-aci.optum.com:16443',
      version: '1.10',
      /* tslint:disable-next-line:max-line-length */
      token: 'eyJhbGciOiJSUzI1NiIsImtpZCI6IiJ9.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJkZWZhdWx0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6ImFkbWluLXVzZXItdG9rZW4tNHA4ZHMiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC5uYW1lIjoiYWRtaW4tdXNlciIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50LnVpZCI6ImQ0MTRmMzFlLWFhYzAtMTFlOC05MmYxLWVjZWJiODk4YTA5NCIsInN1YiI6InN5c3RlbTpzZXJ2aWNlYWNjb3VudDpkZWZhdWx0OmFkbWluLXVzZXIifQ.MTVZ1fA8kSeAGFF9wYkbEyrgOdT7nM5nw-imniUZdguMvjaNbZxcf8-lt1AaiZJLLv1XHXjhSIbzmDEH1-hFMPrZ4CO5injcCBjgR_VuAU_OVGdEH3v-eN1PHKHMm3KBPgJh7jV_6Be9NvX9rKQtRTEGpzzDukdd3Z0j3el5DEgyB-eTwpXXyTYVD7lm5aPM3-iMJTsvYIvx34pD_D47cRmKqrf2M9xzrrOqjPvkiRRDhV34htPFVHXKUdb35H1aelk-3u7PS9jjtzZJBdOvPGkid7ch_GBp_faPj_gTa-x0a_JTBgdE4WjADSEVct0xEcZcZQqR5L_XjdG1Lef1rA',
    },
    GPU: {
      baseUrl: 'https://10.176.22.126:6443',
      version: '1.10',
      /* tslint:disable-next-line:max-line-length */
      token: 'eyJhbGciOiJSUzI1NiIsImtpZCI6IiJ9.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJkZWZhdWx0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6ImFkbWluLXVzZXItdG9rZW4tNWR6bjQiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC5uYW1lIjoiYWRtaW4tdXNlciIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50LnVpZCI6IjdhNDRjMjY5LWNiMjQtMTFlOC04ZDc3LWQwYmY5Y2I3MWMyOCIsInN1YiI6InN5c3RlbTpzZXJ2aWNlYWNjb3VudDpkZWZhdWx0OmFkbWluLXVzZXIifQ.G6Z-M2b54m22BoThSuweu3k7KZ2lnNcQvNP5pu92xAZ_hPoQFidiPgcraxAlRVgGkfQaqnVI1iQFJYAXXap3P_kURH9ee4akYGxoK7Hc7eklWPA6zobilOHBz-aCS7dlDoiQZ4GMKjFi-q1DLzZxecu9NBOO9UUku2a6d76OqIA7NBBcOQDCBCpt0EXtkq9ZLc2wcod464Kv3xBb2J8PLWavEI58-oBeuXWHo4aFGU2VzT7BYIAVLpZp6WY7LfsOtBYG-0HkjZ-GmXOEMSsA5zdU__svAuEcH2kg_ngtfKkHdfTjUcR_eqfQu2bhfb_ZrJnOnt__pw2PYRqo1uQoPw',
    },
  },
};
