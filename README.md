# PracticeSet
Practice Project For Google Associate Android Developer Exam With Networking

## Architecture - MVVM
## Tools
...Retrofit
...Paging 3
...ViewPager
...Glide
...Workmanager

### List Screen
1. Read data using Retrofit from [Devbytes Api](https://devbytes.udacity.com/devbytes.json)
2. Store in Room Database
3. Navigate to Detail Screen when clicked

### Movies Screen
1. Read top-rated and discover movies from [TheMoviesApi](https://api.themoviedb.org/3/)
2. Store in Room Database
3. Retrieve and display with Paging 3

### About Screen
Enter, List and Notification connected by ViewPager 3, BottomNavBar while sharing the same ViewModel

#### Enter Screen
1. Insert/Update user into [GoRestApi](https://gorest.co.in/public/v2/)

#### List Screen
1. Retrieve user data from [GoRestApi](https://gorest.co.in/public/v2/)
2. Retrieve and display with Paging 3
3. Click to navigate user back to enter screen with prepopulated fields for update
4. Long click to select and delete user if present

#### Notification Screen
1. Select time interval to schedule a notification containing the latest user inserted into the [GoRestApi](https://gorest.co.in/public/v2/)
2. Instant Notification retrieves and displays latest user using WorkManager
3. Schedule to set notification based on PeriodicWorkRequest

### Settings Screen
1. Toggle Dark and Light Theme through out the application
