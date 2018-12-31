---
layout: main
title: DTC Expected Error Listing
permalink: /dtc/errors
---

| API | Endpoint | Code | Meaning |
|:---|:---|:---|:---|
| **Ask** | GET /ask/{ask_app_name} | 200 | Success |
||| 404 | Not Found |
||| 401 | Unauthorized |
|| GET /ask/{ask_app_name}/info | 200 | Success |
||| 404 | Not Found |
||| 401 | Unauthorized |
|| GET /ask/{ask_id}/valid/id | 200 | Success |
||| 404 | Not Found |
|| GET /ask/{tmdb_code}/valid/tmdb | 200 | Success |
||| 404 | Not Found |
| **CI** | POST /ci/createJenkinsfile | 200 | Success |
||| 400 | Bad Request |
||| 404 |  Not Found |
||| 401 | Unauthorized |
|| GET /ci/{ci_parent} | 404 | Not Found |
||| 200 | Success |
||| 401 | Unauthorized |
|| POST /ci/{ci_parent}/copy/{ci_from}/to/{ci_job_name} | 200 | Success |
||| 400 | Bad Request |
||| 401 | Unauthorized |
||| 404 |  Not Found |
|| POST/ci/{ci_parent}/copy/{ci_from}/to/{ci_job_name} | 200 | Success |
||| 400 | Bad Request |
||| 401 | Unauthorized |
||| 404 | Not Found |
|| POST /ci/{ci_parent}/create | 201 | Created |
||| 400 | Bad Request |
||| 401 | Unauthorized |
||| 404 | Not Found |
|| POST /ci/{ci_parent}/create/developBranch | 201 | Created |
||| 400 | Bad Request |
||| 401 | Unauthorized |
||| 404 | Not Found |
|| POST /ci/{ci_parent}/create/nonInline | 201 | Created |
||| 400 | Bad Request |
||| 401 | Unauthorized |
||| 404 | Not Found |
|| POST /ci/{ci_parent}/create/parent/{ci_user_or_group} | 201 | Created |
||| 400 | Bad Request |
||| 401 | Unauthorized |
||| 404 | Not Found |
|| POST /ci/{ci_parent}/credentials | 201 | Created |
||| 400 | Bad Request |
||| 401 | Unauthorized |
||| 404 | Not Found |
|| DELETE /ci/{ci_parent}/{ci_job_name} | 200 | Success |
||| 404 | Not Found |
||| 401 | Unauthorized |
|| GET /ci/{ci_parent}/{ci_job_name} | 200 | Success |
||| 404 | Not Found |
||| 401 | Unauthorized |
|| POST/ci/{ci_parent}/{ci_job_name}/build | 201 | Created |
||| 401 | Unauthorized |
||| 404 | Not Found |
| **DTR** | POST/dtr/repo/{dtr_namespace} | 200 | Success |
||| 400 | Bad Request |
||| 404 | Not Found |
||| 401 | Unauthorized |
|| DELETE /dtr/repo/{dtr_namespace}/{dtr_repo_name} | 404 | Not Found |
||| 200 | Success |
||| 401 | Unauthorized |
|| GET /dtr/repo/{dtr_namespace}/{dtr_repo_name} | 404 | Not Found |
||| 200 | Success |
||| 401 | Unauthorized |
| **Codegen** | POST /codegen/code | 200 | Success |
||| 400 | Bad Request |
||| 404 | Not Found |
||| 401 | Unauthorized |
|| POST /codegen/manifest | 200 | Success |
||| 400 | Bad Request |
||| 404 | Not Found |
||| 401 | Unauthorized |
|| POST /codegen/verify | 200 | Success |
||| 400 | Bad Request |
||| 404 | Not Found |
||| 401 | Unauthorized |
| **Github** | GET /github/org/{github_org_name} | 200 | Success |
||| 404 | Not Found |
||| 401 | Unauthorized |
|| POST /github/repo | 201 | Success |
||| 400 | Bad Request |
||| 401 | Unauthorized |
||| 404 | Not Found |
|| POST /github/repo/{github_org_name} | 201 | Created |
||| 400 | Bad Request |
||| 401 | Unauthorized |
||| 403 | Forbidden |
|| POST /github/repo/{github_org_name}/team | 201 | Created |
||| 400 | Bad Request |
||| 401 | Unauthorized |
||| 403 | Forbidden |
|| DELETE /github/repo/{github_org_name}/{github_repo_name} | 204 | Success |
||| 400 | Bad Request |
||| 401 | Unauthorized |
||| 403 | Forbidden |
|| GET /github/repo/{github_org_name}/{github_repo_name} | 200 | Success |
||| 404 | Not Found |
||| 401 | Unauthorized |
||| 403 | Forbidden |
|| PUT /github/repo/{github_org_name}/{github_repo_name}/topics | 201 | Created |
||| 401 | Unauthorized |
||| 403 | Forbidden |
||| 404 | Not Found |
|| DELETE /github/repo/{github_repo_name} | 204 | Success |
||| 400 | Bad Request |
||| 401 | Unauthorized |
||| 403 | Forbidden |
|| GET /github/repo/{github_repo_name} | 200 | Success |
||| 401 | Unauthorized |
||| 403 | Forbidden |
||| 404 | Not Found |
|| GET /github/repo/{github_repo_name}/hooks | 200 | Success |
||| 401 | Unauthorized |
||| 403 | Forbidden |
||| 404 | Not Found |
|| POST /github/repo/{github_repo_name}/hooks | 201 | Created |
||| 401 | Unauthorized |
||| 403 | Forbidden |
||| 404 | Not Found |
|| POST /github/user/{github_org_name}/{github_repo_name}/{github_user_name} | 201 | Created |
||| 401 | Unauthorized |
||| 403 | Forbidden |
||| 404 | Not Found |
|| DELETE /github/user/{github_org_name}/{github_user_name} | 204 | Success |
||| 401 | Unauthorized |
||| 403 | Forbidden |
||| 404 | Not Found |
|| POST /github/user/{github_repo_name}/{github_user_name} | 201 | Created |
||| 401 | Unauthorized |
||| 403 | Forbidden |
||| 404 | Not Found |
| **PAAS** | POST /paas/{paas_platform}/{paas_datacenter}/{paas_zone}/project | 201 | Created |
||| 401 | Unauthorized |
||| 400 | Bad Request |
||| 403 | Forbidden |
||| 404 | Not Found |
||| 422 | Unprocessable Entity |
|| PUT /paas/{paas_platform}/{paas_datacenter}/{paas_zone}/project | 201 | Created |
||| 401 | Unauthorized |
||| 403 | Forbidden |
||| 400 | Bad Request |
||| 404 | Not Found |
||| 422 | Unprocessable Entity |
|| DELETE /paas/{paas_platform}/{paas_datacenter}/{paas_zone}/project/{paas_project_name} | 200 | Success |
||| 401 | Unauthorized |
||| 403 | Forbidden |
||| 404 | Not Found |
||| 422 | Unprocessable Entity |
|| POST /paas/{paas_platform}/{paas_datacenter}/{paas_zone}/project/{paas_project_name}/app | 201 | Created |
||| 401 | Unauthorized |
||| 403 | Forbidden |
||| 400 | Bad Request |
||| 404 | Not Found |
||| 422 | Unprocessable Entity |
|| DELETE /paas/{paas_platform}/{paas_datacenter}/{paas_zone}/project/{paas_project_name}/app/{paas_app_name} | 200 | Success |
||| 401 | Unauthorized |
||| 403 | Forbidden |
||| 404 | Not Found |
||| 422 | Unprocessable Entity |
|| GET /paas/{paas_platform}/{paas_datacenter}/{paas_zone}/project/{paas_project_name}/app/{paas_app_name}/verify | 200 | Success |
||| 401 | Unauthorized |
||| 403 | Forbidden |
||| 404 | Not Found |
||| 422 | Unprocessable Entity |
|| POST /paas/{paas_platform}/{paas_datacenter}/{paas_zone}/project/{paas_project_name}/template | 201 | Created |
||| 401 | Unauthorized |
||| 403 | Forbidden |
||| 404 | Not Found |
||| 400 | Bad Request |
||| 422 | Unprocessable Entity |
|| GET /paas/{paas_platform}/{paas_datacenter}/{paas_zone}/project/{paas_project_name}/verify | 200 | Success |
||| 401 | Unauthorized |
||| 403 | Forbidden |
||| 404 | Not Found |
| **Secure** | POST /secure/group | 200 | Success |
||| 200 | Unauthorized |
||| 200 | Forbidden |
||| 200 | Bad Request |
||| 200 | Not Found |
|| POST /secure/group/add | 200 | Success |
||| 200 | Unauthorized |
||| 200 | Forbidden |
||| 200 | Bad Request |
||| 200 | Not Found |
|| GET /secure/group/{sec_group} | 200 | Success |
||| 401 | Unauthorized |
||| 403 | Forbidden |
||| 404 | Not Found |
|| POST /secure/user | 200 | Success |
||| 401 | Unauthorized |
||| 403 | Forbidden |
||| 404 | Not Found |
|| GET /secure/user/{sec_username} | 200 | Success |
||| 404 | Not Found |
|| GET /secure/user/{sec_username}/member/{sec_lead_emp_id}/{sec_group} | 200 | Success |
||| 401| Unauthorized |
||| 403| Forbidden |
||| 404| Not Found |
| **Template** | /template/ose/{template_paas_platform}/{template_paas_datacenter}/{template_paas_zone}/{template_paas_project_name} | 201 | Created |
||| 401 | Unauthorized |
||| 403 | Forbidden |
||| 404 | Not Found |
| VCS | POST /vcs/group/{vcs_org_name}/{vcs_repo_name}/{vcs_group_name}/read | 201 | Created |
||| 401 | Unauthorized |
||| 403 | Forbidden |
||| 404 | Not Found |
|| POST /vcs/group/{vcs_org_name}/{vcs_repo_name}/{vcs_group_name}/write | 201 | Created |
||| 401 | Unauthorized |
||| 403 | Forbidden |
||| 404 | Not Found |
|| POST /vcs/org | 201 | Success |
||| 401 | Unauthorized |
||| 403 | Forbidden |
||| 400 | Bad Request |
|| DELETE /vcs/org/{vcs_org_name} | 200 | Success |
||| 401 | Unauthorized |
||| 403 | Forbidden |
||| 404 | Not Found |
|| GET /vcs/org/{vcs_org_name} | 200 | Success |
||| 401 | Unauthorized |
||| 403 | Forbidden |
||| 404 | Not Found |
|| POST /vcs/repo/{vcs_org_name} | 201 | Created |
||| 401 | Unauthorized |
||| 403 | Forbidden |
||| 400 | Bad Request |
|| DELETE /vcs/repo/{vcs_org_name}/{vcs_repo_name} | 200 | Success |
||| 401 | Unauthorized |
||| 403 | Forbidden |
||| 404 | Not Found |
|| GET /vcs/repo/{vcs_org_name}/{vcs_repo_name} | 200 | Success |
||| 401 | Unauthorized |
||| 403 | Forbidden |
||| 404 | Not Found |
|| GET /vcs/repo/{vcs_org_name}/{vcs_repo_name}/hooks | 200 | Success |
||| 401 | Unauthorized |
||| 403 | Forbidden |
||| 404 | Not Found |
|| POST /vcs/repo/{vcs_org_name}/{vcs_repo_name}/hooks | 201 | Created |
||| 401 | Unauthorized |
||| 403 | Forbidden |
||| 404 | Not Found |
|| POST /vcs/user/{vcs_org_name}/{vcs_repo_name}/{vcs_user_name}/read | 201 | Created |
||| 401 | Unauthorized |
||| 403 | Forbidden |
||| 404 | Not Found |
|| POST /vcs/user/{vcs_org_name}/{vcs_repo_name}/{vcs_user_name}/write | 201 | Created |
||| 401 | Unauthorized |
||| 403 | Forbidden |
||| 404 | Not Found |

__________________________________________________________________________________

**NOTE:** All of the above APIs can produce a 500 response code which means "Error No response from server" when the servers are  down.  
