# MAD21-Livre-Team04

## Team Members

| Name | Student ID |
|-|-|
| Tan Jie Sheng | S10205049 |
| Kua Li Min | S10206234 |
| Chow Yun Cong | S10194206 |

## Description of The app (Livre)

Livre is an app for readers, tracking their reading habits by logging the length of reading sessions. The process of tracking one's reading habits digitally is easier with the ability to search for books in the catalogue and add it to one's library. Books can also be archived or removed to reduce clutter. Users have the flexibility of adding custom books. The app further enhances the reading experience by providing reading analytics and Popular book listings. Through making reading more hassle free, Livre is a good companion app for everyone who reads, from casual readers to book worms.

## Roles and Contributions

| Name | Roles | Contributions |
|-|-|-|
| Tan Jie Sheng | role | placeholder |
| Kua Li Min | <ul><li>Ideator (come up with general ideas, solutions, and logic)</li><li>Designer (app colour scheme, UI, etc.)</li><li>Padlet submitter</li></ul> | <ul><li>Catalogue Activity, Layouts, API calls and putting response into recyclerview</li><li>Book Details Activity and Layouts for all kinds of books and scenarios</li><li>Customise Book Activity and Layout</li><li>Edit Book Activity and layout</li><li>Sections in DBHandler specific to my activities</li><li>Drawing most images</li><li>Common sections of ReadMe</li></ul> |
| Chow Yun Cong | role | placeholder |

## Other

### App Logo
![Livre Logo](https://user-images.githubusercontent.com/72980567/127664867-c130a866-27ee-44c5-a5c5-2770f68ce1c0.png)


### Colour Scheme

<img src="https://user-images.githubusercontent.com/72980567/127611667-19a48fb0-dba0-4dff-a1d2-33d1da013c7f.png" width="600" >
- Comforting, not alarming<br/>
- Associated with nurturement & academia

### Some Common, Repeated Elements

<img src="https://user-images.githubusercontent.com/72980567/127612422-c2a40495-d836-4a8f-b335-17176cdc1f14.png" width="600" >
<img src="https://user-images.githubusercontent.com/72980567/127616397-050a5477-311b-4811-951c-d468223ba686.png" width="600" >
All tags look like the one above, with different text representing what it does. They are all located at the same spot, along with the Activity Name on the screen while portrait while some shift in landscape mode to allow for more space.

### Brief Explanation of Navigation Between Activities (Li Min)

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
User selects a book in Catalogue or clicks done in Customise (and passed validation) respectively and is brought to book details where the details of their book is displayed (no updated to db at this point). Button to add book is shown while the rest are hidden.
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
User is viewing a custom made book that they have not added yet and decided to go back, Bringing them to the customise page with the details still input, ideal for further edits.
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
*SCENARIO A*<br/>
User edits a Custom book and clicks done (and passed validation) and is brought to book details where the details of their edited book is displayed (db has not been updated).
Book details would show buttons meant for books that are pending a confirmation to save edits.
<img src="https://user-images.githubusercontent.com/72980567/127622511-9fea1028-84ea-4c7d-9afd-3b1deee1d2cf.png"  width="200">

Saving changes would display update bookdetails and DB, and the activity will look like "it will look like this for added custom books:" under **6, 8**.Clicking moving to archive would update the database and stay on the same activity. Start Reading would open the timer (not in my scope) and delete custom book would trigger **11**. Clicking move to archive would update the database and stay on the same activity. Start Reading would open the timer (not in my scope) and delete custom book would trigger **11** scanario C.
<br/>
*SCENARIO B*<br/>
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
