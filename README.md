# Auto Template Admin Interface


This project is divided into two parts:

    * Backend Tomcat Server Api's written in SparkJava
    * Front End written in Angular 12 using nebular / ngx-admin as the template
     
Prerequisites:

    * Angular CLI: 12.2.17
    * Node: 14.21.1
    * Package Manager: npm 6.14.17

Steps to run locally:
    
    To Run Backend:- 
        ** Run maven clean and then, install. Then run jetty server.
    To Run Frontend:-
        ** In ngx-admin folder,
            * Run "npm i" to install the dependencies
            * Run "ng build" 
            * In base-url.service.ts, make changes in following code snippets:
                i)static get(): any {
                    return `http://localhost:8082/ATAdmin/api`;
                }

                ii) if (environment.production) {
                    return `http://localhost:8082/ATAdmin/api`;
                }
            * Run "ng serve" or "npm start", if you use npm start, then you can skip (ng build)
            

Api References:

    All the backend API's are at the location _AppContext_`/api`


This is the highest level of segregation in apis:

    * /generator
    * /entity
    * /search
    * /session
    * /fakeadmin
    * /config
    * /atmapping

Build Links:

Stage Build:- http://ci.internal.media.net/job/KBB/job/ATAdmin-staging-pipeline/
Live Build:- http://ci.internal.media.net/job/KBB/job/ATAdmin-kube-pipeline/

Interface links:

Stage Interface:- http://atinterface-stg.internal.media.net/ATAdmin/web/
Live Interface:- http://atinterface.internal.media.net/ATAdmin/web/
Live Interface cloudflare:- https://atinterface.access.mn/ATAdmin/web

