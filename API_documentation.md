# API Layout

## Resource API

At this moment, the backend service is not implemented to store the file extension of user uploaded images. All image responses have content type "image/jpeg". 

### HTML Interface

returns index.html, namely our frontend resource.

URL: /; /home; /myPosts; /favorite; /notification; /admin

method: GET

### Image Interface

retrieve image by image_id

URL: /image

method: GET

parameter: id={image_id}

request body: null

response body type: image/jpeg

## User Profile Interface

User profile includes username, class year and avatar. Right now avatar update is implemented as a separate interface. This should be merged into the profile update interface in the future.

### Update Profile

URL: /profile

method: POST

parameter: action=add

request body type:  application/json

```json
{
    "id":12,			// must be non-empty
    "username": "John Doe",	// cannot be empty or exceeds 50 characters
    "schoolYear": 2018,		// class year, optional, must be in range [2017, currentYear+5]
}
```

response body

```json
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    }
}
```



### Retrieve Profile

URL: /profile

method: GET

parameter: userId={target_user_id/null}  (when userId=null, returns current user profile)

request body: null

response body: 

```json
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    "body": {
        "id":12,			
        "netId": "xy123",
        "username": "John Doe",
        "major": "Computer Science",
        "schoolYear": "senior",
        "bio": "Hello world",
        "avatarImageIds": "12,13,14",
        "isAdmin": false			// this field only appears when retreived by current user
    }
}
```



### Upload Profile Avatar Image

URL: /avatar

method: POST

parameter: null

request body type: multipart/form-data

```json
{
	"file": "Your binary file"
}
```

response body: 

```json
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    }
}
```



### Retrieve Current User Profile Avatar Image

URL: /avatar

method: GET

parameter: null

request body type: null

response body type: image/jpeg

comment: if avatar does not exist, return default image; default image path is configurable in application-prod.yml



## Course, Textbook and Other Tags

### Usage

- Course, textbook and "other" tags can be edited only by admin and are read-only to regular users. 
- Course tag is referenced as the "related course" field in textbook tag
- Textbook tag is required to be attached to an advertisement when a user want to upload an advertisement of the textbook type
- "Other" tag is used to label other different categories of items to be advertised on Arrietty. For example, furniture, notes, stationery, etc. It is an optional field for when uploading an "other" type advertisement . It is also used for filters in search.

### Retrieve Course Information

URL: /course

method: GET

parameter: id={course_id} (when id=null, the interface returns all courses)

request body type: null

response body: 

```json
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    "body": [
        {
            "id":1,
            "courseCode": "CSCI-SHU 360",
            "subject": "Computer Science"
        },
        {
            "id":2,
            "courseCode": "CSCI-SHU 101",
            "subject": "Intro to Computer Science"
        }
    ]
}
```

### Edit Course Information

This interface requires admin privilege 

URL: /course

method: POST

parameter: action={update/delete}  

request body: 

```json
{
    "id":1,	//when this field is empty and action=update, insert a new course
    "courseCode": "CSCI-SHU 360",	// field is not used when action=delete
    "subject": "Computer Science"	// field is not used when action=delete
}
```

response body: 

```json
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    // body will be present only when it is an insert operation
    "body": {
            "id":1,	
            "courseCode": "CSCI-SHU 360",
            "subject": "Computer Science"
        }
    
}
```

### Retrieve Textbook Tags

URL: /textbook

method: GET

parameter: id={textbook_id} (when id is null, return all textbook tags)

request body:

```json
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
   
    "body": [
        {
            "id":1,
            "title": "Intro to Pyschology",
            "isbn":"571-324234-B32",
            "author": "John",
            "publisher": "Centric",
            "edition":"3rd edition",
            "originalPrice": 123.05,
            "courseId": 2		// id of related course
        },
        {
            "id":2,
            "title": "Intro to Computer Science",
            "isbn":"571-324234-B32",
            "author": "John",
            "publisher": "Centric",
            "edition":"3rd edition",
            "originalPrice": 123.05,
            "courseId": 2
        },
        
    ]
    
}
```

### Edit Textbook Tags

This interface requires admin privilege

URL: /textbook

method: POST

parameter: action={update/delete}  

request body: 

```json
{
    "id":1, //when this field is empty and action=update, insert a new textbook
    "title": "Intro to Pyschology",	// not null
    "isbn":"571-324234-B32",	// not null
    "author": "John",
    "publisher": "Centric",
    "edition":"3rd edition",
    "originalPrice": 123.05,
    "courseId": 12	
}
```

response body: 

```json
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    // body only presents in response to insertion
    "body": {
        "id":1, 
        "title": "Intro to Pyschology",	
        "isbn":"571-324234-B32",	
        "author": "John",
        "publisher": "Centric",
        "edition":"3rd edition",
        "originalPrice": 123.05,
        "courseId": 13		
    }
}
    
```

### Retrieve Other Tag

URL: /otherTag

method: GET

parameter: id={other_tag_id} (when id=null, the interface returns all other tags)

request body type: null

response body: 

```json
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    "body": [
        {
            "id":1,
            "name": "Furniture"
        },
        {
            "id":2,
            "name": "Notes"
        }
    ]
}
```

### Edit Other Tag

This interface requires admin privilege

URL: /otherTag

method: POST

parameter: action={update/delete}  

request body: 

```json
{
    "id":1,	// when action=update and id field is emtpy, insert a new tag
    "name":"Furniture"		
}
```

response body: 

```json
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    // body field only presents in response to insertion operation
    "body": {
        "id":1,
        "name":"Furniture"		
    }
}
```

### 

## Advertisement 

### Usage

Arrietty provide the following features:

- Advertisement upload, update and delete
- Search advertisements through keywords and filters
- Suggest autocomplete options from user keyword inputs
- Retrieve advertisements uploaded by current user
- Retrieve the lasted modified timestamp of all advertisements. Frontend scripted requests this interface every 30 seconds to check if new advertisements are uploaded. If so, a new red spot is generated on the home icon in the navigation bar to notify users.

### Retrieve Current User Ads

URL: /myAdvertisement

method: GET

parameter: null

request body type:  null

response body:

```json
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    "body":[
        	{
                    "id": 1,
                    "adType":"textbook",
                    "adTitle": "Some random ad title",
                    "textbookTitle": "Operating Systems",	// can be empty depend on ad type
                    "isbn":"123-12313-111",				// can be empty depend on ad type
                    "author": "John Doe",					// can be empty depend on ad type
                    "publisher": "Cengage",				// can be empty depend on ad type
                    "edition": "3rd edition",				// can be empty depend on ad type
                    "originalPrice": 1000,				// can be empty depend on ad type
                    "relatedCourse": "CSCI-369,CSCI-101",	// comma separated course codes, can be empty depend on ad type
                    "otherTag": null, 					// can be empty depend on ad type
                    "imageIds": "12,13,14,15",				// comma separated 
                    "price": 666,
                    "comment": "This a really nice book.",	// can be empty
                    "createTime": "Apr 23, 2022 11:25:26 AM",
                    "numberOfTaps": 12						// #tap by other users
        	},
        	{
                    "id": 2,
                    "adType":"other",
                    "adTitle": "A really nice full mirror",
                    "textbookTitle": null,	// can be empty depend on ad type
                    "isbn":null,				// can be empty depend on ad type
                    "author": null,					// can be empty depend on ad type
                    "publisher": null,				// can be empty depend on ad type
                    "edition": null,				// can be empty depend on ad type
                    "originalPrice": 1000,				// can be empty depend on ad type
                    "relatedCourse": null,	// comma separated course codes, can be empty depend on ad type
                    "otherTag": "Furniture", 					// can be empty depend on ad type
                    "imageIds": "12,13,14,15",				// comma separated 
                    "price": 666,
                    "comment": "This a really nice mirror.",	// can be empty
                    "createTime": "Apr 23, 2022 11:25:26 AM",
                    "numberOfTaps": 14						// #tap by other users
                }
    ]
}
```




### Ad Upload

URL: /advertisement?action=add

method: POST

parameter: action=add

request body type:  multipart/form-data

```json
{
    "isTextbook": true,					
    "tagId": 12,						// cannot be empty if ad is related to a textbook
    "adTitle": "Selling an Operating System Textbook!!", 	// cannot be empty
    "images": "actual image files",		// ad images, binary files		
    "price": 256,						// cannot be empty, must be a value in range [0,10000] inclusive
    "comment": "Nothing really.."		// cannot be empty
}
```

response body:

```json
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    }
}
```

### Ad Edit

URL: /advertisement?action=update

method: POST

parameter: action=update

request body type:  multipart/form-data

```json
{
    "id": 12,							// field cannot be empty		
    "images": "actual image files",		// ad images, binary files, non-empty
    "price": 256,						// cannot be empty
    "comment": "Nothing really.."		// cannot be empty
}
```

response body:

```json
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    "body": {
        "id":1,
        "isTextbook": true,
        "tagId": 12,			// tag id related to a textbook or other used item type
        "imageIds":"1,12,4,55",	// comma separated ids
        "price": 256,
        "comment": "Nothing really..",
        "numberOfTaps": 0,
        "createTime": "Apr 23, 2022 11:25:26 AM"
    }
}
```

### Ad Delete

URL: /advertisement?action=delete

method: POST

parameter: action=delete

request body type:  multipart/form-data

```json
{
    "id": 12,							// field cannot be empty		
}
```

response body:

```json
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    "body": null
}
```

### Retrieving Advertisement Last Modified Timestamp

URL: /lastModified

method: GET

parameter: null

request body type:  null

response body:

```json
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    "body":"Apr 23, 2022 11:25:26 AM"		// timestamp of the lastest ad upload action
}
```

### Auto-completion Suggestion 

URL: /suggest

method: POST

parameter: type={textbook/other}; keyword={user_input}

request body: null

response body:

```json
 {
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    "body": ["suggested keyword 1", "suggested keyword 2", "suggested keyword 3"]
    }
    
}
```

### Search

URL: /search

method: POST

parameter: null

request body: 

```json
{
    "adType": "textbook/other", 		// the type of ad searched
    "keyword": "calculus",				// search keyword
    "priceOrder":"asc",					// "asc" for ascending order, "desc" for descending order, no sorting when this field is empty
    "minPrice": 0,						// filter abandoned when field is empty
    "maxPrice": 100,					// filter abandoned when field is empty
    "tag": "furniture",					// filter abandoned when field is empty
    "pageNum": 2						// pagination index, starts from 0
}
```

response body:

```json
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    "body": [	// a list of search result, size configurable in application-prod.yml
                {
                    "id": 1,
                    "username": "John Doe",
                    "userNetId": "xy666",
                    "userAvatarImageId": 122,
                    "adType":"textbook",
                    "adTitle": "Some random ad title",
                    "textbookTitle": "Operating Systems",	// can be empty depend on ad type
                    "isbn":"123-12313-111",				// can be empty depend on ad type
                    "author": "John Doe",					// can be empty depend on ad type
                    "publisher": "Cengage",				// can be empty depend on ad type
                    "edition": "3rd edition",				// can be empty depend on ad type
                    "originalPrice": 1000,				// can be empty depend on ad type
                    "relatedCourse": "CSCI-369,CSCI-101",	// comma separated course codes, can be empty depend on ad type
                    "otherTag": null, 					// can be empty depend on ad type
                    "imageIds": "12,13,14,15",				// comma separated 
                    "price": 666,
                    "comment": "This a really nice book.",	// can be empty
                    "createTime": "Apr 23, 2022 11:25:26 AM",
                    "isMarked": true,						// whether this ad is marked favorite by current user
                    "numberOfTaps": 12						// #tap by other users
                }
        ]
    }
    
}
```



## Notification and Favorite

### Usage

- Arrietty allows user to tap on the advertisement of another user to indicate interest. The identity of the ad owner is only revealed after the tapping. Tapping an ad will also trigger a notification on the owner's side.
- A user may mark and unmark an ad as favorite. 
- An interface that returns a boolean indicating whether current user has new notification

### Tap on an Advertisement

URL: /tap

method: GET

parameter: id={ad_id}

request body:  null

response body:

```json
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    "body":{
        username: "John Doe",
        netId: "yx4141",
        avatarImageId: 12
    }
}
```

### Retrieve Current User Tap Notification

URL: /getNotification

method: GET

parameter: null

request body:  null

response body:

```json
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    "body":[
        {
            "id":1,
            "username": "John Doe",		//info of the tapper
            "netId": "xyx1313",			//info of the tapper
            "avatarImageId": 14,		//info of the tapper
            "adTitle": "A Calculus book in good condition",		// title of the tapped ad
            "createTime": "Apr 23, 2022 11:25:26 AM"			// time of the tap
        },
        {
            "id":2,
            "username": "John Smith",
            "netId": "xyx1223",
            "avatarImageId": 44,
            "adTitle": "A Calculus book in ok condition",
            "createTime": "Apr 23, 2022 11:25:26 AM"
        }
    ]
}
```

### Determining If There is New Notification

Currently, frontend script calls this interface every 30 seconds to check if there is new notification incoming.

URL: /hasNew

method: GET

parameter: null

request body:  null

response body:

```json
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    "body": true	// whether there is new notification at this moment
}
```

### Mark & Unmark an Ad as Favorite

URL: /mark

method: GET

parameter: id={ad_id}; status={on/off} (on for mark, off for unmark)

request body:  null

response body:

```json
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    }
}
```

### Retrieve Favorite Ads of Current User

URL: /getFavorite

method: GET

parameter: null

request body:  null

response body:

```json
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    "body": [	
                {
                    "id": 1,
                    "username": "John Doe",
                    "userNetId": "xy666",
                    "userAvatarImageId": 122,
                    "adType":"textbook",
                    "adTitle": "Some random ad title",
                    "textbookTitle": "Operating Systems",	// can be empty depend on ad type
                    "isbn":"123-12313-111",				// can be empty depend on ad type
                    "author": "John Doe",					// can be empty depend on ad type
                    "publisher": "Cengage",				// can be empty depend on ad type
                    "edition": "3rd edition",				// can be empty depend on ad type
                    "originalPrice": 1000,				// can be empty depend on ad type
                    "relatedCourse": "CSCI-369,CSCI-101",	// comma separated course codes, can be empty depend on ad type
                    "otherTag": null, 					// can be empty depend on ad type
                    "imageIds": "12,13,14,15",				// comma separated 
                    "price": 666,
                    "comment": "This a really nice book.",	// can be empty
                    "createTime": "Apr 23, 2022 11:25:26 AM",
                    "isMarked": true,						// whether this ad is marked favorite by current user
                    "numberOfTaps": 12						// #tap by other users
                }
        ]
    }
    
}
```

## Bulletin

A simple feature for admin to make global announcements

### Read Bulletin Info

URL: /bulletin

method: GET

parameter: null

request body:  null

response body:

```json
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    "body": [
        {
            "id":1,
            "title": "announcement title",
            "content": "announcement text",
            "createTime": "Apr 23, 2022 11:25:26 AM"	// last modification time
        },
        {
            "id":2,
            "title": "announcement title",
            "content": "announcement text",
            "createTime": "Apr 23, 2022 11:25:26 AM"	// last modification time
        }
    ]
}
```

### Edit Bulletin

This interface requires admin privilege

URL: /bulletin

method: POST

parameter: action={update/delete}

request body:  

```json
{
    "id":2,		// when action=update, omission of id represents insertion
    "title": "announcement title",
    "content": "announcement text",
}
```

response body:

```json
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    "body": {
            "id":1,
            "title": "announcement title",
            "content": "announcement text",
            "createTime": "Apr 23, 2022 11:25:26 AM"	
        }
}
```

## Admin Utilities

- Admin user can edit course, textbook and "other" tags (see [Course, Textbook and Other Tags](#Course, Textbook and Other Tags))
- Admin can edit bulletin (see [Bulletin](#Bulletin))
- Admin can read and write user blacklist (a list for blocking users who violated site policy)
- Admin can read site statistics

### Edit Blacklist

This interface requires admin privilege

URL: /updateBlacklist

method: POST

parameter: action={add/delete}; netId={net_id you want to ban}

request body:  null

response body:

```json
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    }
}
```

### Retrieve Blacklist

This interface requires admin privilege

URL: /blacklist

method: GET

parameter: null

request body:  null

response body:

```json
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    "body": [ "sfa1111", "sdfaf32", "ks1234"] // a list of blacklisted netIds
}
```

### Retrieve Daily Site Statistics

- This interface requires admin privilege
- Returns statistics of last 7 days

URL: /adminStatistics

method: GET

parameter: null

request body:  null

response body:

```json
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    "body":[	
        {
            "id":1,
            "totalUserNum":12,
            "loginUserNum":5,
            "adUploadNum":20,
            "adEditNum":11,
            "adDeleteNum":0,
            "totalAdNum":50,
            "tapRequestNum":14,
            "markRequestNum":12,
            "unmarkRequestNum":11,
            "searchRequestNum":40,
            "date":"Apr 23, 2022 00:00:00 AM"
        },
        {
            "id":2,
            "totalUserNum":12,
            "loginUserNum":5,
            "adUploadNum":20,
            "adEditNum":11,
            "adDeleteNum":0,
            "totalAdNum":50,
            "tapRequestNum":14,
            "markRequestNum":12,
            "unmarkRequestNum":11,
            "searchRequestNum":40,
            "date":"Apr 24, 2022 00:00:00 AM"
        }
    ]
}
```



