## Admin标签输入界面

### API

```json
url:/course?id=
method:get
备注：不传id默认获取全部course
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
备注：action={update/delete} action=update时，request body里传id代表修改， 不传代表新增。
request:
{
    "id":1,	//这个不传值代表新增
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
备注：不传id默认获取全部textbook标签
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
备注：action={update/delete} action=update时，request body里传id代表修改， 不传代表新增。
request:

{
    "id":1, //不传值代表新增
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
        "id":1, //不传值代表新增
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
备注：不传id默认获取全部otherTag标签
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
备注：action={update/delete} action=update时，request body里传id代表修改， 不传代表新增。
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



## Advertisement表单增删改查

### API

```json
url:/myAdvertisement
method:get
备注：获取当前请求用户发的所有ad
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
备注：update时id=null默认新增
image 传递方法参见 https://stackoverflow.com/questions/49845355/spring-boot-controller-upload-multipart-and-json-to-dto

update只允许修改images，price， comment
images为全删全增

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

### 接口实现

