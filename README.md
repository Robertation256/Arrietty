# Intro
Arrietty is a second-hand item advertising platform dedicated to the NYUSH student community in the hope of increasing the utilization of second-hand items/textbooks (school can be expensive, especially when you are talking about Calculus hardcopies costing a couple hundred bucks). The website was open to public service around April 2022 and then shut down after we graduated in May (server access revoked). This repo is the backend implementation. Frontend repo can be found [here](https://github.com/juanjuanjks/arrietty-fe).

# Architecture
The backend service is built on top of Spring Boot with MySQL as the primary database. Redis is used for user session caching and fast lookups. Elasticsearch is used for supporting text searches. Finally, Rabbit MQ is introduced to facilitate asynchrounous post processing (ie. synchronization btween MySQL and Elasticsearch)


# APIs

### Advertisement listing search

- url:`/suggest?type=<textbook/other>&keyword=com`
- method:`post`
- request: `null`
- note: at most 10 suggestions at a time
- response: 
```json
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    "body": ["computation theory", "commonsense 101", "comtemporary art"]
    
}
```





- url:`/search`
- method: `post`

- request: 
```json
{
    "adType": "textbook/other", 
    "keyword": "calculus",	
    "priceOrder":"asc/desc", 
    "minPrice": 0,	
    "maxPrice": 100,
    "tag": "furniture",
    "pageNum": 2
}
```

- response: 
```json
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    "body": [
                {
                    "id": 1,
                    "username": "",
                    "userNetId": "",
                    "userAvatarImageId": 12,
                    "adType":"textbook",
                    "adTitle": "",
                    "textbookTitle":"",
                    "isbn":"",
                    "author": "",
                    "publisher": "",
                    "edition": "",
                    "originalPrice": "",
                    "relatedCourse": "CSCI-369,CSCI-101",
                    "otherTag": "",
                    "imageIds": "12,13,14,15",
                    "price": 100,
                    "comment": "",
                    "createTime": "",
                    "isMarked": true,
                    "numberOfTaps": 12
                }
        ]
    }
    
}
```



url: `/lastModified`
method: `get`
request: `null`
response: 
```json
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    "body":"April 03 2000"
}
```


- url: `/tap?id=1`
- method: `get`
- request: `null`
- note: id refers to advertisement id
- response: 
```json
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    "body":{
        "username": "yuechuan",
        "netId": "yz3919",
        "avatarImageId": 12
    }
}
```



## Admin tag edit

### API


- url:`/course?id=`
- method:`get`
- request:`null`
- response:
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
        }
    ]
}
```

- url:`/course?action=delete`
- method:`post`
- note: `action={update/delete}`, when `action=update`, specify id in request body for an update otherwise it is treated as an insert
- request:
```json
{
    "id":1,	
    "courseCode": "CSCI-SHU 360",
    "subject": "Computer Science"
}
```
- response:
```json
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    "body": {
            "id":1,	
            "courseCode": "CSCI-SHU 360",
            "subject": "Computer Science"
        }
    
}
```   
    
- url:`/textbook?id=`
- method:`get`
- request: `null`
    
- response:
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
            "courseId": 2
        }
    ]
}
```

- url:`/textbook?action=delete`
- method:`post`
- request:
```json
{
    "id":1, 
    "title": "Intro to Pyschology",	
    "isbn":"571-324234-B32",	
    "author": "John",
    "publisher": "Centric",
    "edition":"3rd edition",
    "originalPrice": 123.05,
    "courseId": [1,2]		
}
```


- response:
```json
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    "body": {
        "id":1,
        "title": "Intro to Pyschology",
        "isbn":"571-324234-B32",
        "author": "John",
        "publisher": "Centric",
        "edition":"3rd edition",
        "originalPrice": 123.05,
        "courseId": [1,2]		
    }
}
```    
    
    
- url:`/otherTag?id=1`
- method:`get`
- request: `null` 
- response:
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
        }
    ]
}
```


- url:`/otherTag?action=delete`
- method:`post`
- request:
```json
{
    "id":1,
    "name":"Furniture"		
}
```
- response:
```json
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    "body": {
        "id":1,
        "name":"Furniture"		
    }
}
```



## Advertisement listing insert/update/deletion

### API

- url:`/myAdvertisement`
- method:`get`
- request:`null`

- response:
```json
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    "body": [
        {
            "id":1,
            "adTitle": "I want to sell a book",
            "isTextbook": true,
            "tagId": 12,	
            "imageIds":"1,12,4,55",
            "price": 256,
            "comment": "Nothing really..",
            "numberOfTaps": 2,
            "createTime": "dfsdfs"
    
        }
    ]
}
```

- url:`/advertisement?action=<update/delete>`
- method:`post`

- request:
```json
{
    "id":null,
    "isTextbook": true,
    "tagId": 12,
    "images": "image_binary...",		
    "price": 256,
    "comment": "Nothing really.."
}
```

- response:
```json
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    "body": {
        "id":1,
        "isTextbook": true,
        "tagId": 12,
        "imageIds":"1,12,4,55",
        "price": 256,
        "comment": "Nothing really..",
        "numberOfTaps": 0,
        "createTime": "dfsdfs"
    }
}
```

## Image APIs

- url:`/avatar`
- method: `get`
- note: get profile image of the current user

- url:`/avatar`
- method: `post`
- note: update profile image

- url:`/image?id=1`
- method: `get`


### Notification

- url: `/getNotification`
- method: `get`
- request: `null`
- response:
```json
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    "body": [
        {
            "id": 12,
            "username": "Yuechuan Zhang",
            "netid": "yz3919",
            "avatarImageId": 12,
            "adTitle": "I want to get rid of this book ASAP!",
            "createTime": "some timestamp"
        }
    ]
}
```        

- url: `/hasNew`
- method: `get`
- request: `null`
- response: 
```json
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    "body":true
}
```

### Favorite

- url: `/mark?id=12&status=<on/off>`
- method: `get`
- request: `null`
- response: 
```json
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    }
}
```

- url: `/getFavorite`
- method: `get`
- request: `null`
- response:
```json
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    "body": [
        {
                    "id": 1,
                    "username": "",
                    "netId": "",
                    "avatarImageId": "",
                    "adType":"textbook",
                    "adTitle": "",
                    "textbookTitle":"",
                    "isbn":"",
                    "author": "",
                    "publisher": "",
                    "edition": "",
                    "originalPrice": "",
                    "relatedCourse": "CSCI-369,CSCI-101",
                    "otherTag": "",
                    "imageIds": "12,13,14,15",
                    "price":"",
                    "comment":"",
                    "createTime": "",
            		"isMarked": true,
         			"numberOfTaps": 12
                }
    ]
}
```

### Bulletin

- url: `/bulletin`
- method: `get`
- request: `null`
- response: 
```json
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    "body": [
            {
            "id":1,
            "title": "Site Policy and User Agreement",
            "content": "User must provide true and accruate ...",
            "createTime": "xxxx"
        }
    ]
}
```

- url: `/bulletin?action=<update/delete>`
- method: `post`
- request: 
```json
{
    "id": 12,	
    "title": "Site Policy and User Agreement",
    "content": "User must provide true and accruate ...",
}
```
- response: 
```json
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    "body": {
        "id":1,
        "title": "Site Policy and User Agreement",
        "content": "User must provide true and accruate ...",
        "createTime": "xxxx"
    }
}
```

### Manage Blacklist


- url: `/updateBlacklist?action=<add/delete>&netId=yz3919`
- method: `post`
- request: `null`
- response: 
```json
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    }
}
```

- url:`/blacklist`
- method:`get`
- request:`null`
- response: 
```json
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    "body":["yz3919","hhj1981"]
}
```

### Admin Statistics

- url:`/adminStatistics`
- method:`get`
- request:`null`
- response: 
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
            "date":"April 03 2022"
        }
    ]
}
```

