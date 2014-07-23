# Coding Challenge


## Consider the following hypothetical:

A client is planning to make a couple of big expenditures once
they have enough cash, but they also want to ensure they will
have enough money to pay for their rent, groceries, and
planned credit card payments.

Let's assume that their income is "direct deposited" into
their checking account and available for use the next calendar day.

Let's assume that all of their spending comes from their
checking account (no further purchases are made with their credit card).


## Objective:

Provide the client with a timeline of when they can make their desired purchases without:
    - using their credit card
    - dipping below their checking account minimum balance
    - being able to make timely payments on their credit card, rent, and grocery runs

We will need to be able to plugin varying dollar amounts in order to answer the same question for other clients
   (We will assume all timing variables to be constant except for the timing of big purchases)

Provide the checking account ending balance as of Dec. 31, 2015


### Bonus objectives:

    Provide the client's net worth as a daily time series throughout the year
        (calculated as their checking account balance less their credit card balance)

    Provide the credit card ending balance as of Dec. 31, 2015


## Timeframe:
    Assume a hardcoded time frame of:
    Calendar year 2015 (Jan. 1, 2015 through Dec. 31, 2015)



## Use case

Income:
    Client has a take-home pay of $1000 per paycheck.
    They are paid every other Friday.
    Their next paycheck is Friday, Jan 9th, 2015


Desired big purchases:
    1st) $1000 TV
    2nd) $4000 Vacation (taking the vacation will not effect income or expenses)


The client has the following regular expenses:
    $1000 for rent paid on the first of each month
    $200 for groceries, purchased each Saturday


The client currently has the following accounts:
    Checking account:
        $200 required minimum balance
        $1500 starting balance


    Credit card:
        $100 monthly payments due on the 20th of each month
        *for bonus objective:
            15% APR
            $2000 starting balance
            Monthly payments first cover interest expenses, then reduce the card balance.
            Interest expense is calculated by: `statementEndingBalance * APR / 12`


## Input/Output:

Assume inputs will have already been validated and will be a realistic use case.

Be able to accept a JSON file as an input via the program arguments.
    (ex: java -jar target/challenge.jar data/inputs.json)

Send the outputs as JSON to System.out.println().

The "com.learnvest.util.Json" convenience utility class has been provided for convenience.
(See Gson's documentation for the finer details of usage.)

Example JSON files have been provided for convenience:
    - data/inputs.json
    - data/outputs.json

## Libraries:

The following libraries have been provided for convenience.  Feel free to use alternatives.

    - [Gson](https://code.google.com/p/google-gson/)
    - [JodaTime](http://www.joda.org/joda-time/)
    - [Guava](https://code.google.com/p/guava-libraries/wiki/GuavaExplained)
    - [JUnit](https://github.com/junit-team/junit/wiki/Getting-started)
    - You may use `./run` in a separate console window to have your program automatically re-test/run whenever your source code changes

## Submission:

Please zip or tarball your source files (excluding target and/or project directories).  Return as an email attachment to your hiring contact at LearnVest.


