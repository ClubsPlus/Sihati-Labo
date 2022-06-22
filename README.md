# Sihati-Labo
And android app that can be used as a COVID status Sanitary Pass, it employs a color scheme to determine the health status of the user, which allows us to identify the uninfected and permits them to access the University. 

It consists of parts: User, and Laboratory, which will be synchronized with one another, each one with its own application, and this is the Laboratory side.

here you will find the Client Side : [github.com/Tweeel/Sihati-Client](https://github.com/Tweeel/Sihati-Client)

While the user will receive a notification every week to take a test, the status will be changed to "Not tested" status, Then he has to take an appointment in one of the laboratories listed based on their schedule, While each laboratory can make his schedule with a limited number of people in each phase, and each time the laboratory edit or delete any schedule, all the users signed up in that schedule will receive a notification to inform them, once he makes the test, His status will be changed to "Pending" status, and when the result is ready the laboratory upload the result and the user will get a notification informing that his results are ready and the state will automatically update for the user, In case the user is positive the administration will directly receive an email that that student/teacher is contaminated and the status change to "Positive" with the number of quarantine days required. 
Assuredly. 

# The Main Functions Are To Be Realized By The Actor
1- User<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.Create account(Email Confirmation is required)<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.Log in<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.Change password<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.See the current status<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.Get a notification when it is time to take a test <br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.Get a notification when the test result is ready <br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.Get a notification when one of his appointment get changed or deleted<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.See the history of tests<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.make an appointment for a test in one of the laboratories schedules available<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.Cancel an appointment<br>

2- Laboratory<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.Create account<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.Log in<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.Create a schedule <br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.Edit a schedule<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.Delete a schedule<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.See the list of people for each schedule<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.Set the person state from not tested to pending<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.Set the result for the pending person<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.Edit the result of a test<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.See tests history<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.See Pending tests<br>

# Technologies
Kotlin<br>
FireBase<br>
MVVM design pattern<br>
RetroFit<br>
Corountine<br>
LiveData<br>
ViewBinding<br>

# Images
<img width="1622" alt="auth" src="https://user-images.githubusercontent.com/75279465/174917249-5c6696a5-8281-4876-8257-6a3ddeb2fbd8.png">
<img width="1614" alt="labo1" src="https://user-images.githubusercontent.com/75279465/174917520-1a76b029-87b7-4ef6-aee8-f2ec9e8ed47f.png">
<img width="1616" alt="labo2" src="https://user-images.githubusercontent.com/75279465/174917537-c9c23d41-bde4-4593-bead-cb0151266d6e.png">
