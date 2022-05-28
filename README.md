# Jyra Project Management Tool (JPMT)

## Project description
Nowadays as we see a rapid growth in software development, the organizations should beprovided with flexible software to manage their teams and projects. 
The Jyra Project Management Tool provides the ability for users to define and manage new Projects, Sprintsand Tasks, as well as to report their progress. In addition to 
that it allows users to register, and administrators to manage them. It implements a web-based front-end client using Thymeleaf templates. Each page has a distinct URL, 
and the routing between pages is done server side using SpringMVC. The backend is implemented as a REST using JSON data serialization.

## Main user roles (actors in UML)
- Anonymous Users – can only view the information page, login and register.
- Developers (extends User) – can add new Sprints and Tasks, as well as to verify/report completion of TaskResults and SprintResults for different Tasks and Sprints
- Product Owner (extends User) – can add new Projects and Sprints and Tasks, as wellas to verify/report completion of TaskResults, SprintResults and ProjectResults for 
different Tasks, Sprints and Projects.
- Administrator (extends User) – can add new Projects. In addition can manage all User's data, except their passwords.

## Architecture
Jyra PMT follows a layered architecture in which each layer communicates with the layer directly below or above (hierarchical structure) it. There are three layers in 
Jyra PMT are as follows:
- Presentation Layer - In short, it consists of views i.e., frontend part.
- Business Layer - It consists of service classes and uses services provided by data access layers.
- Database Layer - In the database layer, CRUD (create, retrieve, update, delete) operations are performed.
