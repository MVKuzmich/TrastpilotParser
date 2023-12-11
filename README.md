# Trustpilot parser application
The application parses information about the number of views and domain rating on the Trustpilot website. 
The user receives data in JSON format. 
If the domain is not available on the site, an appropriate response is returned. 
To increase application performance and reduce the number of requests, caching of received data is used.
# Technology stack
- **Spring Boot WebFlux** - for asynchronous operations
- **Caffeine** - for data caching
- **Jsoup** - for html parsing
- **Lombok**

