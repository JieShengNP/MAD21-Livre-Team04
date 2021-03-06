# MAD21-Livre-Team04

## App Link
https://play.google.com/store/apps/details?id=sg.edu.np.mad.livre

## Team Members

| Name | Student ID |
|-|-|
| Tan Jie Sheng | S10205049 |
| Kua Li Min | S10206234 |
| Chow Yun Cong | S10194206 |

## Description of The app (Livre)

Livre is an app for readers, tracking their reading habits by logging the length of reading sessions. The process of tracking one's reading habits digitally is easier with the ability to search for books in the catalogue and add it to one's library. Books can also be archived or removed to reduce clutter. Users have the flexibility of adding custom books and editing them. The app further enhances the reading experience by providing reading analytics and Popular book listings. Users' reading data is saved on the cloud, preserving their precious reading logs, that can still be erased if they choose to. Through making reading more hassle free, Livre is a good companion app for anyone who reads, from casual readers to book worms.

## Roles and Contributions

| Name | Roles | Contributions |
|-|-|-|
| Tan Jie Sheng | <ul><li>Debugger</li><li>Application Tester</li><li>CI Automation with GitHub Actions</li></ul> | <ul><li>Library and Archive Activity (Showing of User's Books)</li><li>Navigation Drawer for Specific Scenarios</li><li>Book Database Creation</li><li>Firebase User Accounts</li><li>Integration of Database with Firebase Database</li><li>Showing of Public Analytics</li></ul> |
| Kua Li Min | <ul><li>Ideator (come up with general ideas, solutions, and logic)</li><li>Designer (app colour scheme, UI, etc.)</li><li>Padlet submitter</li></ul> | <ul><li>Catalogue Activity, Layouts, API calls and putting response into recyclerview</li><li>Book Details Activity and Layouts for all kinds of books and scenarios</li><li>Customise Book Activity and Layout</li><li>Edit Book Activity and layout</li><li>Sections in DBHandler specific to my activities</li><li>Drawing certain images</li><li>Common sections of ReadMe</li><li>Landscape versions of my layouts (configuration changes) with minimal impact</li></ul> |
| Chow Yun Cong | <ul><li>Music Sourcer</li><li>Application Tester</li><li>App Publisher</li></ul> | <ul><li>Timer(Main) Activity(timing and recording)</li><li>Music Player and Disclaimer</li><li>Logs Database Creation</li><li>Showing of Logs(History)</li><li>Showing of Individual Statistics</li></ul> |




## Other

### App Logo
![Livre Logo](https://user-images.githubusercontent.com/72980567/127664867-c130a866-27ee-44c5-a5c5-2770f68ce1c0.png)


### Colour Scheme

<img src="https://user-images.githubusercontent.com/72980567/128291575-2cb449f1-06e3-4da8-8c7b-ae51887ee794.png" width="600" >
- Comforting, not alarming<br/>
- Associated with nurturement & academia

### Some Common, Repeated Elements

<img src="https://user-images.githubusercontent.com/72980567/128291792-b82312df-578b-4a2c-95e9-a8da79be054c.png" width="600" >
<img src="https://user-images.githubusercontent.com/72980567/128291645-5c66cb83-b265-4cfb-8349-e3853386456f.png" width="600" >

All tags look like the one above, with different text representing what it does. They are all located at the same spot, along with the Activity Name on the screen while portrait while some shift in landscape mode to allow for more space.

Consistent UI makes UX better as users can rely on intuition. Tags make navigation easier.

### Catalogue Animations

<img src="https://user-images.githubusercontent.com/72980567/128292740-9ca5435b-26cb-4e14-abf3-684014760482.gif" width="200" >
<img src="https://user-images.githubusercontent.com/72980567/128293059-68fcddf7-5b34-478f-b080-f218394c99fb.gif" width="200" >
<img src="https://user-images.githubusercontent.com/72980567/128293652-2aed5a19-bffb-4da3-87f7-404ed9bbffc5.gif" width="200" >


All tags look like the one above, with different text representing what it does. They are all located at the same spot, along with the Activity Name on the screen while portrait while some shift in landscape mode to allow for more space.




### Li Min

## Video for high user input activities (catalogue, customise, edit)
[a](https://user-images.githubusercontent.com/72980567/128299578-31ce26c2-599e-4f97-95bf-c48968d76cca.mp4)

## Brief Explanation of Navigation Between Activities (Li Min)

<img src="https://user-images.githubusercontent.com/72980567/127614329-7c791051-02b0-481a-a7b1-5a6376b2c898.png" width="600" >
(Library can be replace with archive in most circumstances. For example, clicking catalogue while in achive then clicking back would bring one back to archive. However, this does not apply to scenarios B, C, and D in 

**12**. They will redirect to Library no matter what.)
<br/>

**1**
<br/>
User clicks the catalogue button in the library and redirects to catalogue. <br/>
<img src="https://user-images.githubusercontent.com/72980567/127615065-8ada1373-efd0-4777-9570-b1b4007d36f0.png" width="200" >
<br/>

**2**
<br/>
User clicks library tag/button or back button from Catalogue, bringing them to Library
<br/>

**4**
<br/>
User chooses to Customise a book while in Catalogue (their search ended up with no queries, the search did not have the book they wanted, or an error occured with searching) <br/>
<img src="https://user-images.githubusercontent.com/72980567/127615722-950e3d84-55bb-4893-8882-d1976cd42fad.png" width="200" >
<br/>

**3,5**
<br/>
User clicks back (button or tag) when in Customise (and confirmed to delete any unsaved changes) or in Book Details respectively, and get brought back to Catalogue.
<br/>

**6,8**
<br/>
User selects a book in Catalogue or clicks done in Customise (and passed validation) respectively and is brought to book details where the details of their book is displayed (no updated to db at this point). Button to add book is shown while the rest are hidden. Book covers added get made into bitmaps and resized to prevent crashing as putextras.
<br/>
<img src="https://user-images.githubusercontent.com/72980567/127618246-092b7ec7-8a47-4f07-bec7-fdeee4bd51a8.png" width="200" >
<img src="https://user-images.githubusercontent.com/72980567/127620153-3cf548b8-8ad2-4812-8dd4-a509090a916b.png" width="200" >
<br/>
Results that are custom books appear as this at the top of of the recyclerview, with an overlay saying "Custom"

<img src="https://user-images.githubusercontent.com/72980567/127618710-057aa75f-f16a-413a-8ca2-f5ec4c01fd7b.png" width="200" >
<br/>
Book details for books not added
If user decides to add the book, DB is updated, book details will look like this for added non custom books:
<img src="https://user-images.githubusercontent.com/72980567/127619905-64187ad2-0196-42ad-8dcf-0ca3aafa60c2.png" width="200" >
<br/>

it will look like this for added custom books:
<br/>
<img src="https://user-images.githubusercontent.com/72980567/127620509-4a4e04b4-fa9f-4b61-a7fb-6824751385f2.png" width="200" >
<br/>

The wrench at the top right is to edit the custom book.
<br/>
Clicking move to archive would update the database and stay on the same activity. Start Reading would open the timer (not in my scope) and delete custom book would trigger **11**.

<br/>

**7**
<br/>
User is viewing a custom made book that they have not added yet and decided to go back, Bringing them to the 
page with the details still input, ideal for further edits.
<br/>

**10**
<br/>
User edits a Custom book and clicks done (and passed validation) and is brought to book details where the details of their edited book is displayed (db has not been updated).
Book details would show buttons meant for books that are pending a confirmation to save edits.

<br/>

<img src="https://user-images.githubusercontent.com/72980567/127622511-9fea1028-84ea-4c7d-9afd-3b1deee1d2cf.png" width="200" >
<br/>

**9**
<br/>

User, while on edit page decided to go back and confirms to delete unsaved changes (if any), user would be brought back to bookdetails with book unchanged.
<br/>

**11**
<br/>
*SCENARIO A*<br/>
User decides to go back when in bookdetails with Library as the previous activity.

*SCENARIO B*<br/>
User decides to go back from bookdetails after just saving changed to a book or adding a customised book, which brings them to the library (and clears activity stack).

*SCENARIO C*<br/>
User deletes custom book or saves changes to custom book and decides to go back.

*SCENARIO D*<br/>
User ends up in Bookdetails with no other activities below the stack, going back would redirect the user to Library.
<br/>

**12**
<br/>
User clicks on book in Library, catalogue button in the library and redirects to Book details showing information about to book.




### Library and Archive
#### Library
This is the Library, where all the books that the user has added will be shown.<br/>
<img src="https://user-images.githubusercontent.com/73047869/127776350-c6d7f239-292f-4c46-9b25-e48119813ace.PNG" width = "200"><br/>
#### Archive
This is the Archive, where all the books that the user has moved to library will be shown. This is usually used as a place for the user to store books that they are currently not reading and also for decluttering of library.<br/>
<img src="https://user-images.githubusercontent.com/73047869/127776418-54a11d2d-358b-4af5-aef7-e2d2dc2f36cc.PNG" width = "200"><br/>

### Cloud Data and Statistics Features
<img src = "https://user-images.githubusercontent.com/73047869/127775587-d76bc0a5-98fe-4b1b-a288-ec3f02c2b716.PNG" width = "200">

#### Popular Books
This page shows the top books the general public has been reading with the Livre app and sorted by most readers, then most time spent.<br/>
<img src = "https://user-images.githubusercontent.com/73047869/127775960-280e7c5f-d011-4d8b-89d7-381dab17d225.PNG" width = "200"><br/>
#### My Statistics
This page shows the user's personal reading statistics.<br/>
<img src = "https://user-images.githubusercontent.com/73047869/127776069-7ed70a41-e4db-410c-b0d8-aa680d96dbae.PNG" width = "200"><br/>
#### Save to Cloud
This option will copy the user's current book list and records list into the online database.<br/>
#### Download from Cloud
This option will replace all the user's current data with what the user last uploaded into the online database.<br/>
#### Delete Local Data
This option will delete the user's current data in the database, if the user wishes to restore their latest cloud data, they can still download from cloud as long as they do not save to cloud after deleting.<br/>
#### Log out
This option will log the user out of the app and will redirect them back to the sign in page.<br/>




### Credits
All music tracks used are Royalty Free and are licensed under Creative Commons License. <br />
All music tracks used belong to Ron Gelinas <br />
Another Day (Original Mix) <br />
Beacon of Light Original Mix) <br />
Easy (Original Mix) <br /> 
Endeavour (Original Mix) <br />
Equinox (Original Mix) <br /> 
Evening Out (Original Mix) <br /> 
Far Away (Original Mix) <br /> 
New Direction (Instrumental) - (Original Mix) <br /> 
The Art of Healing (Original Mix) <br />
Windsurfing (Original Mix) <br /> 
SoundCloud : <a href = "https://soundcloud.com/atmospheric-music-portal">Ron Gelinas Chillout Lounge</a> <br /> 
Youtube: <a href = "https://www.youtube.com/user/iamRottenRon">Ron Gelinas Chillout Lounge</a>

Some logos were taken from Microsoft Office.
