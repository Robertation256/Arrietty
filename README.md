### Elasticsearch mapping

更新ES mapping instruction

- 删除旧mapping： 

​		method：delete

​		url: localhost:9200/advertisement

- 新增mapping：

  method： put

  url: localhost:9200/advertisement

  body: 带上下面的数据

```json
{
    "mappings": {
        "properties": {
            "ad_title": {"type": "text"},
            "is_textbook": {"type":"boolean"},
            "user_id": {"type": "long", "index": "false"},
            "textbook_tag": {
                "properties": {
                    "title": {"type": "text"},
                    "isbn": {"type": "keyword"},
                    "author": {"type": "text", "index":false},
                    "publisher": {"type": "text", "index":false},
                    "edition": {"type": "text", "index":false},
                    "original_price": {"type": "scaled_float", "scaling_factor": 100, "index":false},
                    "related_course": {
                        "properties": {
                            "course_code": {"type": "keyword"},
                            "course_name": {"type": "text"},
                            "subject": {"type": "keyword"}
                        }
                    }
                }
            },  
            "other_tag": {"type": "keyword"},
            "image_ids": {"type":"text", "index": false},
            "price": {"type": "scaled_float", "scaling_factor": 100},
            "comment": {"type": "text", "index": false},
            "create_time": {"type":"date"}
             
        }
      }
}
```



### Advertisement搜索接口

```json
url:/suggest?type=<textbook/other>&keyword=com
method:post
request: null
备注：最多返回10条suggestion

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
    "priceOrder":"asc/desc", //为空则不排序
    "minPrice": 0,	// 为空代表用户未采用这个filter
    "maxPrice": 100,// 为空代表用户未采用这个filter
    "tag": "furniture",// 为空代表用户未采用这个filter
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
                    isMarked: true，
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
            "adTitle": "I want to sell a book",
            "isTextbook": true,
            "tagId": 12,	// 后端组装
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

## 图片接口

```json
url:/avatar
method: get
备注：获取当前用户头像

url:/avatar
method: post
备注：修改当前用户头像

url:/image?id=1
method: get
备注：获取id对应图像资源
```

### Notification

```json
url: /notification
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


url: /favorite
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

#### 实现

favorite

on

- 检查 adId 是否存在
- 插入数据库（重复抛出异常）
- 修改redis 用户favorite 缓存

off

- 检查adId是否已被mark
- 删除数据库数据，修改redis



favorite

- favorite 表拉取数据，advertisement service匹配

ES 增加 isMarked



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

