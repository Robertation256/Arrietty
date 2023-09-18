# Intro
Arrietty is a second-hand item advertising platform dedicated to the NYUSH student community in the hope of increasing the utilization of second-hand items/textbooks (school can be expensive, especially when you are talking about Calculus hardcopies costing a couple hundred bucks). The website was deployed around April 2021 and then shut down after we graduated in May (server acces was revoked). This repo is the backend implementation. Frontend repo can be found [here](https://github.com/juanjuanjks/arrietty-fe).

# Architecture
The backend service is built on top of Spring Boot with MySQL as the primary database. Redis is used for user session caching and fast lookups. Elasticsearch is used for supporting text searches. Finally, Rabbit MQ is introduced to facilitate asynchrounous post processing (ie. synchronization btween MySQL and Elasticsearch)


# APIs

### Advertisement listing search

```json
url:/suggest?type=<textbook/other>&keyword=com
method:post
request: null
note： at most 10 suggestions at a time

response: 
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    "body": ["computation theory", "commonsense 101", "comtemporary art"]
    }
    
}






url:/search
method: post

request: 
{
    "adType": "textbook/other", // not null
    "keyword": "calculus",	// not null
    "priceOrder":"asc/desc", 
    "minPrice": 0,	
    "maxPrice": 100,
    "tag": "furniture",
    "pageNum": 2
}


response: 
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    "body": [
                {
                    id: 1,
                    username:
                    userNetId:
                    userAvatarImageId:
                    adType:"textbook",
                    adTitle: "",
                    textbookTitle:"",
                    isbn:"",
                    author:
                    publisher:
                    edition:
                    originalPrice:
                    relatedCourse: "CSCI-369,CSCI-101",
                    otherTag:
                    imageIds: "12,13,14,15"
                    price:
                    comment:
                    createTime:
                    isMarked: true,
                    numberOfTaps: 12
                }
        ]
    }
    
}


url: /lastModified
method: get
request: null
response: 
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    "body":"April 03 2000"
}

url: /tap?id=1
method: get
request: null
备注: id is advertisment id
response: 
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    "body":{
        username: "yuechuan"
        netId: "yz3919"
        avatarImageId: 12
    }
}
```



## Admin tag edit

### API

```json
url:/course?id=
method:get
request:null

response:
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


url:/course?action=delete
method:post
note: action={update/delete} when action=update, carry in id request body for update otherwise it is treated as a insert
request:
{
    "id":1,	
    "courseCode": "CSCI-SHU 360",
    "subject": "Computer Science"
}

response:
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
    
    
url:/textbook?id=
method:get

request: null
    
response:
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


url:/textbook?action=delete
method:post

request:

{
    "id":1, 
    "title": "Intro to Pyschology",	// not null
    "isbn":"571-324234-B32",	// not null
    "author": "John",
    "publisher": "Centric",
    "edition":"3rd edition",
    "originalPrice": 123.05,
    "courseId": [1,2]		
}



response:
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    "body": {
        "id":1,
        "title": "Intro to Pyschology",	// not null
        "isbn":"571-324234-B32",	// not null
        "author": "John",
        "publisher": "Centric",
        "edition":"3rd edition",
        "originalPrice": 123.05,
        "courseId": [1,2]		
    }
}
    
    
    
url:/otherTag?id=1
method:get
request: null
    
response:
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



url:/otherTag?action=delete
method:post
request:

{
    "id":1,
    "name":"Furniture"		
}



response:
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

```json
url:/myAdvertisement
method:get
request:null

response:
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


url:/advertisement?action=<update/delete>
method:post

request:
{
    "id":null,
    "isTextbook": true,
    "tagId": 12,
    "images": 传递方法见备注,		
    "price": 256,
    "comment": "Nothing really..",
}


response:
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

```json
url:/avatar
method: get
note: get profile image of the current user

url:/avatar
method: post
note: update profile image

url:/image?id=1
method: get

```

### Notification

```json
url: /getNotification
method: get
request: null
response:
response:
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    "body": [
        {
            id: 12,
            username: "Yuechuan Zhang",
            netid: "yz3919",
            avatarImageId: 12,
            adTitle: "I want to get rid of this book ASAP!",
            createTime: xxx
        }
    ]
}
        
url: /hasNew
method: get
request: null
response: 
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    "body":true
}
```

### Favorite

```json
url: /mark?id=12&status=<on/off>
method: get
request: null
response: 
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    }
}


url: /getFavorite
method: get
request: null
response:
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    "body": [
        {
                    id: 1,
                    username:
                    netId:
                    avatarImageId:
                    adType:"textbook",
                    adTitle: "",
                    textbookTitle:"",
                    isbn:"",
                    author:
                    publisher:
                    edition:
                    originalPrice:
                    relatedCourse: "CSCI-369,CSCI-101",
                    otherTag:
                    imageIds: "12,13,14,15"
                    price:
                    comment:
                    createTime:
            		isMarked: true,
         			numberOfTaps: 12
                }
    ]
}
```

### Bulletin

```json
url: /bulletin
method: get
request: null
response: 
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
            "createTime": xxxx
        }
    ]
}

url: /bulletin?action=<update/delete>
method: post
备注： 新增时无需传入id
request: 
{
    id: 12,	// 新增时无需传入id
    "title": "Site Policy and User Agreement",
    "content": "User must provide true and accruate ...",
}

response: 
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    "body": {
        "id":1,
        "title": "Site Policy and User Agreement",
        "content": "User must provide true and accruate ...",
        "createTime": xxxx
    }
}
```

### Manage Blacklist

```json
url: /updateBlacklist?action=<add/delete>&netId=yz3919
method: post
request: null
response: 
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    }
}

url:/blacklist
method:get
request:null
response: 
{
    "responseStatus": {
        "status": "Ok",
        "message": "Success"
    },
    "body":["yz3919","hhj1981"]
}
```

### Admin Statistics

```json
url:/adminStatistics
method:get
request:null
response: 
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

