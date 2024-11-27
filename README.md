
<h1 align="center">XBCAD7319 Final POE - SparkLine - The A Team 👋</h1>

## SparkLineHR

### Table of Contents
- [Description of the app](#description-of-the-app)
- [Features](#features)
- [Design Considerations](#design-considerations)
  - [User Interface (UI) and User Experience (UX)](#user-interface-ui-and-user-experience-ux)
  - [Data Handling](#data-handling)
  - [Testing and Quality Assurance](#testing-and-quality-assurance)
- [Installation](#installation)
  - [Prerequisites](#prerequisites)
  - [Steps](#steps)
- [Usage](#usage)
- [Authors and Acknowledgement](#authors-and-acknowledgement)
- [Support](#support)
- [Project Status](#project-status)


## Description of the app

SparkLine HR is a platform that has been created to service the company SparkLine Finance, and this platform will solve their problem of using paper-based documentation to track employee information such as their timesheets, payslips, and leave requests. It will solve the problem by providing a web application for HR to control employee data, and accept requests made by the user, along with creating performance reviews, releasing payslips, and posting training courses that have been completed. The employees will be able to view their own information such as payslips and performance reviews, as well as make timesheet/overtime submissions, and leave request submissions. 

<p align="right">(<a href="#table-of-contents">back to top</a>)</p>

## Features

* Self-Service Portal: The Self-Service Portal will allow users to view their personal information, update contact and emergency contact details, view released payslips, and view pending overtime and leave requests.
* Timesheet Submission: This feature will allow users to submit their hours worked for specific weeks, and will send an overtime request to HR if the worked hours are over 40.
* Leave Request Submission: This feature will allow users to submit leave requests of different types, and will be forced to submit a doctor's note for sick leave.
* Development and Goals: Users can view training courses that have been completed and uploaded by hr, as well as performance reviews uploaded by them, and they can track their own personal goals in this feature.
* Incident and Feedback Reporting: This feature will allow users to report incidents to be viewed by HR on the web app, these incidents can either include the employee number, or be reported anonymously.
<p align="right">(<a href="#table-of-contents">back to top</a>)</p>

## Design Considerations

When designing the SparkLineHR app, several key considerations were made:

### User Interface (UI) and User Experience (UX)
* Intuitive Navigation: The app features a simple and intuitive navigation structure to enhance user experience. Users can easily access payslip viewing, timesheet submission, and other features of the app.
* Responsive Design: The UI is designed to be responsive across various devices, ensuring a consistent experience on smartphones and tablets.

### Data Handling
* API Integration: The app integrates two APIs:
  * Custom Onboarding API: Handles user onboarding and login, and will send an email to first-time users with a one-time PIN to log into the app with before changing passwords
  * Custom Email API: This API will send users emails regarding Overtime Requests and Leave Requests that are declined or accepted by HR in order to notify them.
* Data Models: Defined data classes (e.g., Payslip, Timesheet, Leave Request) facilitate data management and ensure that the app correctly processes and displays information from Firebase.

### Testing and Quality Assurance
* Unit Testing: Unit tests are implemented to verify the functionality of critical features, such as Firebase database connections to ensure that data is written and read correctly.
* Automated Testing: Integration tests are executed on every push and pull request to ensure consistent performance and reliability of the app.
<p align="right">(<a href="#table-of-contents">back to top</a>)</p>

## Installation

There are 2 ways to get the app:
### Prerequisites

* Internet Connection: Ensure your device has an active internet connection.
* Play Store Account: A Google account signed in on the Play Store.

### Steps

* Open the Play Store: Launch the Google Play Store app on your Android device.
* Search for SparkLineHR: In the search bar at the top, type “SparkLineHR” and hit search.
* Select the App: Locate the SparkLineHR app from the search results (usually marked with the SparkLineHR logo) and tap on it.
* Install the App: Tap the Install button to download and install SparkLineHR on your device.
* Open the App: Once installation is complete, you can open SparkLineHR directly from the Play Store by tapping Open or from your app drawer.


**OR**

### Prerequisites

* Android Studio Installed
* Github

### Steps

1. Clone the repository.
`git clone https://github.com/ST10071160/xbcad-poe-theateam-android.git`

2. Open the project in Android Studio.
3. Run the application and launch it on an emulator or a connected Android smartphone.

<p align="right">(<a href="#table-of-contents">back to top</a>)</p>

## Usage

The user can then log into the app using credentials provided by HR when the app loads. Use the dashboard for:

* Submitting timesheets and leave requests to HR
* View and update employee information, and view released payslip
* View performance reviews, training courses, and goals
* Add goals and submit incident reports to HR
<p align="right">(<a href="#table-of-contents">back to top</a>)</p>

## Authors and Acknowledgement

👤 **ST10048211** - Anjali Sunil Morar

👤 **ST10071160** - Aidan Johann Schwoerer

👤 **ST10104776** - Mohamad Aslam Mustufa Khalifa

👤 **ST10243270** - Aayush Navsariwala

👤 **ST10062860** - Abdullah Gadatia

**Final POE Links:**
- **Project Links:**
	
	Final Project Android: https://github.com/ST10071160/xbcad-poe-theateam-android.git
	Final Project Web App: https://github.com/aayush-nav/xbcad7319-poe-the_a_team-webApp.git

- **YouTube Links:**

	Android App Demonstration Video: 

	API and Web App Demonstration Video:  




## Support

Email - the.a.team.bcad@gmail.com

<p align="right">(<a href="#table-of-contents">back to top</a>)</p>

## Project Status

XBCAD7319 - Project Completion

<p align="right">(<a href="#table-of-contents">back to top</a>)</p>
