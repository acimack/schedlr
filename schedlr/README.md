# cs0320 Term Project 2021

**Team Members:**

Annie Cimack (acimack),
Ethan Mullen (emullen2),
Julia Robbin (jrobbin),
Virak Pond-Tor (vpondtor)

**Team Strengths and Weaknesses:**

Annie:
- Strengths - documentation, code hierarchy
- Weaknesses - testing (pbt), containerization

Ethan:
- Strengths - UI/front end, debugging
- Weaknesses - testing, documentation

Julia:
- Strengths - logic/problem solving, pbt
- Weaknesses - system testing

Virak:
- Strengths - SQL
- Weaknesses - testing, file structure, graphic design


**Project Idea(s):**
### Idea 1 (Selected)

As someone who easily gets overwhelmed by the work that needs to get done, my idea is based around 
helping students (or just people in general) manage the tasks that are on their plate. 
In order to do this, I would love to create a project that allows users to tell the app 
what needs to get done, by when, how long it might take, as well as time they have to do 
the work and have the program create a schedule for the user to help them better manage their time.

Core functionality:
- Task input (with deadlines and priority)
- Sorting by class (or other divider)
- Index creation and display
- Division of smaller task within a large project
- Adjustability (allowing the user to override or update the tasks/when the tasks are)

Some things that could be interesting/challenging within this project is how to 
handle various conflicts, dynamic creation of the schedule, and adjustment as things 
change. One main feature would be to help manage larger projects by making sure that 
smaller tasks are done on time to allow for completion on time. Another potential feature 
could be a sharing or social aspect such that if a task needs to be done with others, the 
schedules could work together to find shared free time to complete the work (or from a 
social standpoint - when two people have time to just hang out).

**HTA Approval (dpark20):** Idea approved – make sure to include good depth in your algorithm for managing the schedule beyond just "whatever works"!

### Idea 2
Courses @ Brown (CAB) has an overwhelming amount of information that could be solved through 
data analytics. This idea would entail a Chrome extension that works with CAB to provide 
data analysis on course offerings to help students improve the shopping experience. 
Possible features include:
- Searching through previous offerings of the course to provide past syllabi.
- Showing that classes are prerequisites for other classes (i.e. looking at CS33 and 
  it showing you that it is a prereq for Operating Systems, Security, etc.).
- Providing a map from primary cart courses with estimated walking times.
- Being able to easily view a professor's history (Have they taught this class before? 
  What other classes have they taught?)
- Looking at average hours for primary courses in the cart to check if overall workload is
  manageable
- Ability to add notes on primary courses

Algorithmically, this analysis would use data structures like graphs and trees to find 
relevant information. The most challenging part of implementing this idea would be the 
web scraping required and the creation of an extension, since that differs from the web
pages we have been creating.

**HTA Approval (dpark20):** Idea approved contingent on adding more algorithmic complexity beyond just web scraping. You should do more with the information that you have, or access it in a more complex way. Update README with a better specified algorithmic component beyond just saying what data structures to use!

Great ideas! Looking forward to seeing what you build :)

**Mentor TA:** _Put your mentor TA's name and email here once you're assigned one!_

## Meetings
On your first meeting with your mentor TA, you should plan dates for at least the following meetings:_

**Specs, Mockup, and Design Meeting:** _(Index for on or before March 15)_

**4-Way Checkpoint:** _(Index for on or before April 5)_

**Adversary Checkpoint:** _(Index for on or before April 12 once you are assigned an adversary TA)_


## Specifications
Project selected: Schedlr, a schedule management tool.

- Creates an optimized schedule based on the user’s data

- Two views for the users to-do list and schedule
  
- Add tasks to the users to-do list

- Create schedule from input

- Incorporation of personal calendar

- Marking tasks as complete and update schedule

- Preferences for how the schedule is created


## How to Build and Run

We have created a Makefile for building and running convenience. 

To build the backend, run `mvn package` (or `make build`).

To run the backend, run `./run --gui` (or `make run`)

To build the frontend, run `cd frontend && npm install` (or `make set-up-react`)

To run the frontend, run `cd frontend && npm start` (or `make react`)


> The Google API is in test mode.
> Therefore, Google accounts used to authenticate with our login system must be entered in the cloud console as a test user.
> Contact [ethan_mullen@brown.edu](ethan_mullen@brown.edu) for more information if you want to use your account with schedlr.


## Testing

For backend testing, we focused on using Unit testing to test our algorithm. Due to the random nature of the schedule
outputs, we were not able to implement system testing for the backend. Thus, in order to test the algorithm, we
conducted Unit testing on individual methods and for parts of the program that return randomized data we tested 
based on expected properties in order to ensure that we were getting the outputs that we would expect (or that fit within
our specifications for the algorithm). We also tested the algorithm using the frontend (once a connection was setup) by
testing different cases in order to ensure that the produced schedules were as expected.

For the frontend, we tested usability by conducting different use cases and ensuring that all functionality was working.

To run our tests, run `mvn test`. One thing to note is that, if running the tests from terminal, one of the tests
will produce an error when it is accessing the Calendar. This error is unrelated to our code, and our tests pass despite the 
error. In order to avoid getting this error, the tests can be fun from intellij using the green play button on the test
folder located at `src/test`

## Browsers used to test

We tested using Macs and Google Chrome.

## Division of Labor
*It is important to note that our team functioned in such a way where any given component was worked on by multiple people. What
follows is a rough break down of the tasks that each team member focused on.

Ethan: Google API integration, frontend styling

Annie: Frontend/backend integration, frontend functionality

Virak: Database, logging in

Julia: Scheduling algorithm/testing, backend functionality, frontend functionality/styling

## Project Organization

`/data` contains the database.

`/frontend` contains the React project. 
With that directory, `/src` has directories for each of the three pages: Dashboard, Login, and Onboard.
`App.js` contains a `Router` (with `react-router-dom`) for each of the three pages.

`/src` contains the backend of the project. `/main/java` contains the project separated into packages for the algorithm, database, google API, and the main class.
`/test` contains the JUnit tests.

## Sources
Google Integration: https://developers.google.com/calendar/overview
Salting and Hashing: https://subscription.packtpub.com/book/application_development/9781849697767/1/ch01lvl1sec10/adding-salt-to-a-hash-intermediate
