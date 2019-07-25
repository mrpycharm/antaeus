## Antaeus

Antaeus (/ænˈtiːəs/), in Greek mythology, a giant of Libya, the son of the sea god Poseidon and the Earth goddess Gaia. He compelled all strangers who were passing through the country to wrestle with him. Whenever Antaeus touched the Earth (his mother), his strength was renewed, so that even if thrown to the ground, he was invincible. Heracles, in combat with him, discovered the source of his strength and, lifting him up from Earth, crushed him to death.

Welcome to our challenge.

## The challenge

As most "Software as a Service" (SaaS) companies, Pleo needs to charge a subscription fee every month. Our database contains a few invoices for the different markets in which we operate. Your task is to build the logic that will pay those invoices on the first of the month. While this may seem simple, there is space for some decisions to be taken and you will be expected to justify them.

### Structure
The code given is structured as follows. Feel free however to modify the structure to fit your needs.
```
├── pleo-antaeus-app
|
|       Packages containing the main() application. 
|       This is where all the dependencies are instantiated.
|
├── pleo-antaeus-core
|
|       This is where you will introduce most of your new code.
|       Pay attention to the PaymentProvider and BillingService class.
|
├── pleo-antaeus-data
|
|       Module interfacing with the database. Contains the models, mappings and access layer.
|
├── pleo-antaeus-models
|
|       Definition of models used throughout the application.
|
├── pleo-antaeus-rest
|
|        Entry point for REST API. This is where the routes are defined.
└──
```

## Instructions
Fork this repo with your solution. We want to see your progression through commits (don’t commit the entire solution in 1 step) and don't forget to create a README.md to explain your thought process.

Please let us know how long the challenge takes you. We're not looking for how speedy or lengthy you are. It's just really to give us a clearer idea of what you've produced in the time you decided to take. Feel free to go as big or as small as you want.

Happy hacking 😁!

## How to run
```
./docker-start.sh
```

## Libraries currently in use
* [Exposed](https://github.com/JetBrains/Exposed) - DSL for type-safe SQL
* [Javalin](https://javalin.io/) - Simple web framework (for REST)
* [kotlin-logging](https://github.com/MicroUtils/kotlin-logging) - Simple logging framework for Kotlin
* [JUnit 5](https://junit.org/junit5/) - Testing framework
* [Mockk](https://mockk.io/) - Mocking library


## Experience, solutions and approaches
Using kotlin the very first time was an amazing experience for me. I am always thrilled to learn new languages. Coming form a Python background, I was used to a lot of run time exceptions and errors due to lack of type checking in Python. However, kotlin reduces these error to an exponential value with its amazing type checking system. As a developer I was able to spend more time for coming up with a solution as compared to debugging the issues. Since, I have never used Kotlin before, this gave me an awesome opportunity to explore this new language.

### Refactoring
During this challenge I found a few things (from my experience) in the code that needed a little bit of refactoring. The major refactorings are:
##### Logging
Added logging throughout the code (request logging for every request that is served via the Javalin server, debug and info statements throughout the code in order to log important informationm that could be used to debug any issues when faced).
##### DAL separation
Since the application had a single Data Access Layer, it could become very cumbersome when we scale. That single DAL class would have hundreds and thousands of functions. I divided this DAL into two different DALs for now. `InvoiceDal` and `CustomerDal`. This would help us keep one DAL's functionality isolated from the others.


### Schema
I will list down the enhancements and functionalities I added in order to support charging the subscription fee.
##### A new invoioce status
Added a new invoice status called `RETRY`. The reason behind adding this status was to keep track of those invoices that failed due to network exceptions or any other general exceptions. The invoices with a `RETRY` status would then be retried by a scheduler after every five minutes. One thing that I think **could** have improved the functionality was to add retry attempts on those invoices and a progressive timeout on the scheduled job.
##### Failure reason for the failed invoices
Added a new field in the `InvoiceTable` called `failureReason`. The rational behind adding this field was to keep track of the reason due to which an invoice charge was failed. For now, the unique failure reasons are as follows:
* `INSUFFICIENT_BALANCE`
* `CUSTOMER_NOT_FOUND`
* `CURRENCY_MISMATCH`
* `NONE` (default)

### Schedulers
Added a whole new package for job scheduling in the app. For now there are two schedulers that will be initialized upon app initialization and schedule later jobs.
* `BillingScheduler`: A scheduler that will be scheduled to run at every 1st of the month, process pending invoices and, re-schedule for the next month.
* `BillingRetryScheduler`: A scheduler that will be scheduled to run after every 5 minutes and retry all the invoices that failed due to network or any other general exception.
###### Drawbacks:
There is a one major draw back of these scheduler. **This would never scale**. Assuming we have more than one instances of our app running on different machines, we would have N numbers of schedules, scheduled at a given time. As a result, race conditions are possible. To avoid this, we can have a single instance of our app on a separate machine (no access via APIs) which will be the only instance running the schedules. 
###### Note:
Invoices that are failed due to `CustomerNotFoundException` or `CurrencyMismatchException` or insufficient balance are not marked with a `RETRY` status. These are marked as `FAILED` with a failure reason accordingly. These invoices can then be made available for display for customer support or monitoring via some back-office portal.
### Billing service
Implemented the following functions in the billing service:
* `chargePendingInvoices`: a public function that will be called via the billing scheduleder.
* `retryInvoices`: another public function that will be called via the retry billing scheduler.
* `processInvoice`: a private function that will get the invoices from either of the two functions mentioned above and process it (charge via payment provider) using coroutines.

### Closure
This was an amazing challenge for me. I am sure there definitely a few things that I have missed or I could have done in a more efficient way. Nevertheless, I am very excited to find out the results and find out what my shortcomings are and what are the areas that I can improve.
