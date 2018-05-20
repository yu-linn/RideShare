# rideShare / Team 21

## Iteration 01 - Review & Retrospect

 * When: 25 October 2016, 10pm
 * Where: Online via Slack

## Process - Reflection

Over the course of this first iteration, we have mainly focused on planning the design features of our app.
In essence, what our app should accomplish and what features it will have. We also have focused on the UI
and general layout a little bit, creating a few mockups for it. Towards the end of this iteration we have
now started discussing the technical implementation of our project, as well as getting some of the code
set up on Android Studio.

Note that we have labelled this bullet points in order of importance.

Decisions turned out well:

 - Having everyone experiment with different UI designs and applications allowed us to create a good
 amount of both high fidelity and low fidelity mock-ups, and spurred discussion within our group for
 UI choices. [Mock-up artifacts can be found in the 'mockups_designs' folder of this repo]
 - The decisions we have made on planning out the structure and integration of our software has turned
 out really well so far. A map of how we intend users to experience our application can be found in
 basicUIMockup.txt under the 'mockups_designs' directory. As well, by splitting up the research on the
 various APIs we want to use (Facebook, Google) we have come up with a lot more ideas on what we can
 reasonably accomplish. Kinvey's API was another great tool we used that was a quick way to manage several
 of our backend services such as data storage, db queries and also provide better security for user
 login/signup management.
 - The initial plans for our back-end code and classes on Android Studio look good, as we are starting
 to create CRC cards to guide the general structure of our code. This should help us a lot in the
 next phase where we construct the backbone of our application. The CRC cards can be found in the
 'mockups_designs' directory as well under crc.docx or crc.pdf.

Decisions did not turn out as well as you hoped:

 - "Investigate algorithm design for how to match up drivers and riders in order to find the most optimal groups"
 this idea was mentioned in our initial planning meeting [iteration-01.plan.md]. However, we ended up changing
 the design of our application so that rather than matching random people together based on distance, instead it
 will be based on a request system so that riders can request a ride from a carpooler and those drivers can accept
 or deny it.
 - Also in the initial planning meeting [iteration-01.plan.md] we mentioned that we wanted to have continuous discussions
 through Slack, Facebook, and Github. Though we realized that splitting it up over three applications can get confusing.
 When trying to organize online meetings, there can be mix ups on when / where we are chatting.
 - We planned on researching the API for Facebook in our first planning meeting, but we realized through the research that
 in order to get permissions on user's Facebook event, we have to send out an application and request this right directly
 from Facebook. Although this is achievable, we decided to focus on other more crucial implementations at this stage.
 Regardless, we did get a lot of useful features and ideas such as Facebook signup and login from looking at their API,
 so it was only a minor hiccup.

We are planning to make the following changes to our process:

 - As a group we have had some problems in assigning tasks to each other. Mainly, some members do better by
 self-motivation and others do better by having themselves assigned clear tasks. We have recently added in
 a Trello board and linked it to our slack channel which allows any / all of us to add tasks that need to
 be done immediately. As well, others can go in and assign themselves to tasks as they see fit. It should
 also give us a better visualization of what everyone is working on at any given moment to reduce confusion.


## Product - Review

Goals/tasks that were met/completed:

 - Created both high and low fidelity mockups
 - Designed user experience maps and layout of UI
 - Researched and prototyped the use of Facebook and Google Maps APIs as well as figuring out the ways in which
 we want to integrate them.
 - Set up a basic project in Android Studio for us to collaborate on + added in support for Kinvey.

Goals/tasks that were planned but not met/completed:

 - Concrete roles for everyone. We have a general idea of what we want to be working on, but at least for the
 first phase, everyone seemed to work on a little bit of everything. We do not think we will come up with specific
 role titles for everyone, but for the next phase we think we definitely will split into groups (frontend / backend, etc)
 - Do not have a concrete database model. We discussed this in our meeting today, but it took us a while to figure out
 how we wanted to store data, but this should be finalized in the next week or so.

(Optional) Goals/tasks that were not originally planned, but ended up being not met/completed:

 - Came up with basic coding plans for our backend by designing some CRC cards.


Going into the next iteration, our main insights are:

 - Write and implement the fundamental classes that drive our application (User, Admin, Event, etc).
 - Design and implement some sort of database model into our application.
 - Experiment using APIs in the application (Example, test out google maps to plan routes for drivers) and see
 what works well enough to integrate into our project.
 - Research into Android material design pattern and make some changes to our UI designs to provide the best user experience.
 - Start implementing our UI design into the project, expected that this will change a lot still so best to keep it flexible.
 - Regular and productive use of the Trello board for tracking these tasks.
