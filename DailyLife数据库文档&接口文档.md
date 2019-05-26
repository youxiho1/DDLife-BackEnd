## 数据库设计

### 用户表User

| 名称        | 类型   | 备注                                    |
| ----------- | ------ | --------------------------------------- |
| openId      | string | primary key openid竟然是个字符串2333333 |
| session     | string |                                         |
| token       | string |                                         |
| token_time  | date   | 生成token的时间（有效期一天？）         |
| private_key | string | 生成token的私钥                         |

建表语句

CREATE TABLE `clockin`.`User` (
  `openId` VARCHAR(200) NOT NULL,
  `session` VARCHAR(200) NOT NULL,
  `token` VARCHAR(200) NOT NULL,
  `token_time` DATE NOT NULL,
  `private_key` VARCHAR(200) NOT NULL,
  PRIMARY KEY (`openId`),
  UNIQUE INDEX `openId_UNIQUE` (`openId` ASC));

### 习惯表Habit

| 名称         | 类型    | 备注                                                     |
| ------------ | ------- | -------------------------------------------------------- |
| id           | integer | primary key autoincrement                                |
| user_id      | string  | 记录用户的openId                                         |
| name         | string  | 习惯的名字                                               |
| icon         | integer | 图标对应的编号                                           |
| category     | integer | 分类（0--任意时间，1--早，2--中，3--晚）                 |
| weekday      | integer | 周几提醒，7位二进制（1代表提醒，0代表不提醒），从Mon-Sun |
| create_time  | date    | 创建时间                                                 |
| clockin_days | integer | 坚持天数（总计的打卡天数）                               |
|              |         |                                                          |

CREATE TABLE `clockin`.`Habit` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_id` VARCHAR(200) NOT NULL,
  `name` VARCHAR(200) NOT NULL,
  `icon` INT NOT NULL,
  `category` INT NOT NULL,
  `weekday` INT NOT NULL,
  `create_time` VARCHAR(45) NOT NULL,
  `clockin_days` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`));

HabitDao测试情况

addHabit√	deleteById√	updateHabit√	clockIn√	findAllHabitByUserId√	findById√	findTodayHabit√

### 打卡记录表Record

| 名称         | 类型          | 备注                      |
| ------------ | ------------- | ------------------------- |
| id           | integer       | primary key autoincrement |
| user_id      | string        | 记录用户的openid          |
| habit_id     | integer       | 习惯的id                  |
| description  | VARCHAR（20） | 打卡日志 可以为空         |
| clockin_time | VARCHAR（20） | 打卡时间                  |
| clockin_date | VARCHAR（20） | 打卡日期                  |

CREATE TABLE `clockin`.`Record` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_id` VARCHAR(200) NOT NULL,
  `habit_id` INT NOT NULL,
  `description` VARCHAR(20) NULL,
  `clockin_time` VARCHAR(45) NOT NULL,
  `clockin_date` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`));

RecordDao测试情况

addRecord√	deleteTodayById√	getInsistDays√	isTodayClockIn√	deleteByHabitId√	findRecordByMonth

### 计划表Plan

| 名称        | 类型        | 备注                      |
| ----------- | ----------- | ------------------------- |
| id          | int         | primary key autoincrement |
| icon        | int         | 图标id                    |
| title       | varchar(10) | 计划标题                  |
| desp        | varchar(34) | 计划具体描述              |
| deadline    | string      | deadline                  |
| flag_finish | tinyint(1)  | 是否已完成                |
| finish_time | string      | 完成的时间                |
| create_time | string      | 创建时间                  |
| user_id     | int         | 用户id                    |

CREATE TABLE `clockin`.`Plan` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `icon` INT NOT NULL,
  `title` VARCHAR(10) NOT NULL,
  `desp` VARCHAR(34) NULL,
  `deadline` VARCHAR(45) NOT NULL,
  `flag_finish` TINYINT(1) NOT NULL DEFAULT 0,
  `finish_time` VARCHAR(45) NULL,
  `user_id` VARCHAR(200) NOT NULL,
  `create_time` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`));

PlanDao测试情况

addPlan√ updatePlan√ finish√ cancelFinish√ delay√ findTodayPlan√ findAllPlan√ hasPermission√ isFinish√ findById√ deletePlan√


## 接口设计

#### 状态码总述

1 成功

-1 数据库操作失败（在dao中出现了异常）

-2 参数错误（get或post传过来的参数有问题）

-3 身份信息校验错误（session中拿不到对应的token或解析出现问题）

-4 未知错误（被最大的Exception所catch住了）

-5 权限错误（这个id对应的东西不是你的），或操作与实际状态不符（如你已经点赞了还要点赞）

-6 这不是今天该打卡的习惯或这不是今天该完成的计划

-7 特殊状态码：打卡操作时当前不在打卡时间段内

-8 不存在该id对应的计划或习惯（也有可能是因为这个id所对应的习惯或计划不是你的）

-9 特殊状态码：修改deadline的时候不能把deadline修改到过去

#### 登录

现阶段登录未完成，但是你可以用(手动滑稽)

请求路径：/api/login

请求方法：get

直接访问就好啦，我会给你set一个默认的session，现在可以用它进行测试

//以下内容请忽略

参数：code（就是调用wx.login()得到的那个临时的code，注：这个code的有效时间只有5分钟）

返回：token

#### 登出

现阶段登录未完成，但是你可以用（手动滑稽*2）

请求路径：/api/logout

请求方法：get

直接访问就好啦，我会把session清掉，现在可以用它进行测试

**接下来所有内容带上token**

#### 查询今日习惯√

请求内部逻辑：HabitDao-findTodayHabit

请求路径：/api/habit/getHabit?kind=1

请求方法：get

请求参数：无

返回值：共计四个JSON数组，分别是allTime, morning, noon, evening

每一个数组中是该时间段的习惯，每一条习惯的内容如下：

| 名称        | 说明                                           | 数据类型 |
| ----------- | ---------------------------------------------- | -------- |
| createTime  | 该习惯创建的时间                               | string   |
| flag_today  | 今天是否打卡了                                 | bool     |
| name        | 习惯名字                                       | string   |
| icon        | 习惯图标编号                                   | int      |
| weekday     | 该习惯在一周中的什么时候需要执行               | int      |
| clockinDays | 共计打卡天数                                   | int      |
| id          | 该习惯的id                                     | int      |
| category    | 习惯的分类（任意时间为0、早为1、中为2、晚为3） | int      |
| insistDays  | 连续打卡天数                                   | int      |

注：每个JSON数组内部按照按照创建时间排序

查询成功的返回样例：

```json
{"noon":[{"createTime":"20190520184356","flag_today":false,"name":"name2","icon":1,"weekday":255,"clockinDays":0,"id":8,"category":2,"insistDays":0}],"evening":[{"createTime":"20190520184031","flag_today":false,"name":"name2","icon":1,"weekday":127,"clockinDays":1,"id":6,"category":3,"insistDays":0}],"allTime":[],"morning":[]}
```

查询失败的返回样例：

```JSON
{"desp":"身份信息校验错误","status":-3}
```

#### 查询全部习惯√

HabitDao-findAllHabitByUserId

请求路径：/api/habit/getHabit?kind=2

请求方法：get

请求参数：无

返回值：共计四个JSON数组，分别是allTime, morning, noon, evening

每一个数组中是该时间段的习惯，每一条习惯的内容如下：

| 名称        | 说明                               | 数据类型 |
| ----------- | ---------------------------------- | -------- |
| createTime  | 该习惯创建的时间                   | string   |
| flag_today  | **不能使用**                       | bool     |
| name        | 习惯名字                           | string   |
| icon        | 习惯图标编号                       | int      |
| weekday     | 该习惯在一周中的什么时候需要执行   | int      |
| clockinDays | 共计打卡天数                       | int      |
| id          | 该习惯的id                         | int      |
| category    | 习惯的分类（任意时间、早、中、晚） | int      |
| insistDays  | **不能使用！！！**                 | int      |

**WWWWWarning：该接口中的flag_today和insistDays两条属性不能使用！！！**

注：每个JSON数组内部按照按照创建时间排序

查询成功的返回样例：

```json
{"noon":[{"createTime":"20190520184356","flag_today":false,"name":"name2","icon":1,"weekday":255,"clockinDays":0,"id":8,"category":2,"insistDays":0}],"evening":[{"createTime":"20190520184031","flag_today":false,"name":"name2","icon":1,"weekday":127,"clockinDays":1,"id":6,"category":3,"insistDays":0}],"allTime":[],"morning":[]}
```

查询失败的返回样例：

```JSON
{"desp":"身份信息校验错误","status":-3}
```

#### 添加习惯/修改习惯√

请求内部逻辑：HabitDao-addHabit	HabitDao-updateHabit

请求路径：/api/habit/updatehabit

请求方法：post

请求参数

| 名称     | 说明                                                         | 数据类型 |
| -------- | ------------------------------------------------------------ | -------- |
| id       | 习惯的id（如果是添加习惯这里填0，如果是修改习惯这里填习惯的id） | int      |
| name     | 习惯名字                                                     | string   |
| icon     | 图表编号                                                     | int      |
| category | 时间类型（0代表任意时间，123分别对应早中晚）                 | int      |
| weekday  | 在一周中的什么时候（7位二进制对应的整型，从Mon-Sun，例如：一周七天不要周六，那么这个参数应该填二进制1111101所对应的十进制的值） | int      |

注：修改习惯的时候即使没有改这一项也必须把原来的值传给我

返回：操作执行结果的状态码以及状态描述

状态码约定：1成功，-1数据库操作失败，-2参数错误，-3身份校验错误，-4未知错误

返回样例：

```json
{"desp":"添加习惯成功","status":1}
```

```json
{"desp":"name参数格式非法","status":-2}
```

```json
{"desp":"修改习惯成功","status":1}
```

#### 删除习惯√

请求内部逻辑：HabitDao-deleteById	RecordDao-deleteByHabitId

请求路径：/api/habit/deletehabit

请求方法：get

请求参数：

| 名称    | 说明   | 数据类型 |
| ------- | ------ | -------- |
| habitId | 习惯id | int      |

返回：操作执行结果的状态码以及状态描述

状态码约定：1成功，-1数据库操作失败，-2参数错误，-3身份校验错误，-4未知错误

返回样例：

```json
{"desp":"数据库操作失败","status":-1}
```

```json
{"desp":"habitId参数格式非法","status":-2}
```

```json
{"desp":"删除成功","status":1}
```

#### 打卡/取消打卡√

请求路径：/api/habit/clockin

请求方法：get

打卡请求内部逻辑：RecordDao-isTodayClockIn	RecordDao-addRecord	HabitDao-clockIn

取消打卡请求内部逻辑：RecordDao-isTodayClockIn RecordDao-deleteTodayRecord HabitDao-clockIn

请求参数：

| 名称    | 说明                                 | 数据类型 |
| ------- | ------------------------------------ | -------- |
| kind    | 操作种类（打卡为1，取消打卡为0）     | int      |
| habitId | 习惯id                               | int      |
| desp    | 打卡日志（仅打卡时有此参数，可为空） | string   |

返回：操作执行结果的状态码以及状态描述

状态码约定：1成功，-1数据库操作失败，-2参数错误，-3身份校验错误，-4未知错误，-5打卡状态与操作状态不符合（即今日没打卡，要调用的操作却是取消打卡，注：如果你要打卡的习惯是别人的，也会报状态码为-5），-6这不是今天的习惯，-7这不是该打卡的时间段，-8不存在该id对应的习惯

返回样例：

```json
{"desp":"打卡成功","status":1}
```

```json
{"desp":"权限错误或今日该习惯未打卡","status":-5}
```

```json
{"desp":"这不是今天的习惯","status":-6}
```

```json
{"desp":"当前不在打卡时间段内","status":-7}
```

#### 查看习惯详情（简略版）√

请求内部实现：HabitDao-findById

请求路径：/api/habit/detail

请求方法：get

请求参数：

| 名称 | 说明           | 数据类型 |
| ---- | -------------- | -------- |
| id   | 要查看习惯的id | int      |

返回：习惯名字、图标编号、颜色编号、**共计坚持多少天、当前连续天数**、这个月哪些天签到了（关于这个参数怎么返回，暂时想的是用31位二进制返回给你，这个待定吧，我们再想想有没有更好的实现）、今天是否打卡了

如果查询成功，返回的JSONObject参数如下：

| 名称        | 说明                               | 数据类型 |
| ----------- | ---------------------------------- | -------- |
| createTime  | 该习惯创建的时间                   | string   |
| flag_today  | **不能使用！！！**                 | bool     |
| name        | 习惯名字                           | string   |
| icon        | 习惯图标编号                       | int      |
| weekday     | 该习惯在一周中的什么时候需要执行   | int      |
| clockinDays | 共计打卡天数                       | int      |
| id          | 该习惯的id                         | int      |
| category    | 习惯的分类（任意时间、早、中、晚） | int      |
| insistDays  | **不能使用！！！**                 | int      |

**WWWWWarning：该接口中的flag_today和insistDays两条属性不能使用！！！**

成功返回样例：

```json
{"createTime":"20190523230623","flag_today":false,"name":"9999","icon":9999,"weekday":72,"clockinDays":0,"id":15,"category":1,"insistDays":0}
```

失败返回样例：

注：如果这个id对应的习惯不是你的，status也会是-8

```json
{"desp":"不存在该id对应的习惯","status":-8}
```

#### 查询今日需要完成的计划√

请求内部逻辑：PlanDao-findTodayPlan

请求路径：/api/plan/getplan?kind=1

请求方法：get

请求参数：无

返回值：共计两个JSON数组，分别是unfinished和finished

每一个数组中是该分类下的计划，每一条计划的内容如下：

| 名称        | 说明                                     | 数据类型 |
| ----------- | ---------------------------------------- | -------- |
| create_time | 该计划创建的时间                         | string   |
| desp        | 该计划的描述                             | string   |
| title       | 计划的标题                               | string   |
| icon        | 计划的图标编号                           | int      |
| deadline    | 计划的deadline（精确到天，格式20190709） | string   |
| flag_finish | 该计划是否已经完成                       | bool     |
| id          | 该计划的id                               | int      |
| finish_time | 该计划完成的时间                         | string   |

注：每个JSON数组内部按照按照创建时间排序

查询成功的返回样例：

```json
{"finished":[{"desp":"","create_time":"20190526233732","icon":1,"id":5,"deadline":"20190527","title":"title","flag_finish":true,"finish_time":"20190527004526"}],"unfinished":[{"desp":"999","create_time":"20190526233732","icon":777,"id":9,"deadline":"20190527","title":"999","flag_finish":false,"finish_time":""}]}
```

查询失败的返回样例：

```JSON
{"desp":"身份信息校验错误","status":-3}
```

#### 查询全部计划√

请求内部逻辑：PlanDao-findAllPlan

请求路径：/api/plan/getplan?kind=2

请求方法：get

请求参数：无

返回值：（以下内容说的简直不是人话：）共计两个Type A数组，分别是完成的和未完成的；每个Type A数组中的元素是一个日期加上一个Type B数组，每一个Type B数组中的元素是一个计划，其包含内容如下：

| 名称        | 说明                                     | 数据类型 |
| ----------- | ---------------------------------------- | -------- |
| create_time | 该计划创建的时间                         | string   |
| desp        | 该计划的描述                             | string   |
| title       | 计划的标题                               | string   |
| icon        | 计划的图标编号                           | int      |
| deadline    | 计划的deadline（精确到天，格式20190709） | string   |
| flag_finish | 该计划是否已经完成                       | bool     |
| id          | 该计划的id                               | int      |
| finish_time | 该计划完成的时间                         | string   |

注：每个JSON数组内部按照按照创建时间排序

查询成功的返回样例：

```json
{"finished":[{"date":"20190527","list":[{"desp":"","create_time":"20190526233732","icon":1,"id":5,"deadline":"20190527","title":"title","flag_finish":true,"finish_time":"20190527004526"}]}],"unfinished":[{"date":"20200103","list":[{"desp":"999","create_time":"20190526233732","icon":999,"id":4,"deadline":"20200103","title":"999","flag_finish":false,"finish_time":""},{"desp":"999","create_time":"20190526233732","icon":888,"id":7,"deadline":"20200103","title":"999","flag_finish":false,"finish_time":""}]},{"date":"20190527","list":[{"desp":"999","create_time":"20190526233732","icon":777,"id":9,"deadline":"20190527","title":"999","flag_finish":false,"finish_time":""}]}]}
```

查询失败的返回样例：

```JSON
{"desp":"身份信息校验错误","status":-3}
```

#### 完成计划&取消完成计划√

请求路径：/api/plan/finish

请求方法：get

完成计划请求内部逻辑：PlanDao-finish

取消完成计划内部逻辑：PlanDao-cancelFinish

请求参数：

| 名称   | 说明                             | 数据类型 |
| ------ | -------------------------------- | -------- |
| kind   | 操作种类（打卡为1，取消打卡为0） | intpolan |
| planId | 计划id                           | int      |

返回：操作执行结果的状态码以及状态描述

状态码约定：1成功，-1数据库操作失败，-2参数错误，-3身份校验错误，-4未知错误，-5完成状态与操作状态不符合（即今日没完成，要调用的操作却是取消完成，注：如果你要打卡的习惯是别人的，也会报状态码为-5），-6这不是今天该完成的计划，-8不存在该id对应的计划）

返回样例：

```json
{"desp":"该id的计划不存在","status":-8}
```

```json
{"desp":"该计划的deadline不是今天","status":-6}
```

```json
{"desp":"权限错误或该计划已经完成","status":-5}
```

```json
{"desp":"取消完成该习惯成功","status":1}
```

```json
{"desp":"成功完成该习惯","status":1}
```

#### 添加计划&修改计划√

请求内部逻辑：PlanDao-addPlan PlanDao-updatePlan

请求路径：/api/plan/updateplan

请求方法：post

请求参数：

| 名称     | 说明                                 | 数据类型 |
| -------- | ------------------------------------ | -------- |
| id       | 要修改的计划的id（id=0代表添加计划） | int      |
| title    | 计划的标题                           | string   |
| icon     | 计划的图标编号                       | int      |
| desp     | 计划的描述                           | string   |
| deadline | 计划的deadline（格式举例：20190709） | string   |

请求返回值：执行操作的状态

状态码规定如下：

1成功，-1数据库操作失败，-2参数错误，-3身份校验错误，-4未知错误，-8该用户不存在id为这样的计划，-9不能把deadline调整到过去

返回样例：

```json
{"desp":"添加习惯成功","status":1}
```

```json
{"desp":"该用户不存在id为这样的计划","status":-8}
```

```json
{"desp":"修改习惯成功","status":1}
```

#### 删除计划√

请求内部逻辑：PlanDao-deletePlan

请求路径：/api/plan/deleteplan

请求方法：get

请求参数：

| 名称   | 说明   | 数据类型 |
| ------ | ------ | -------- |
| planId | 计划id | int      |

返回：操作执行结果的状态码以及状态描述

状态码约定：1成功，-1数据库操作失败，-2参数错误，-3身份校验错误，-4未知错误

返回样例：

```json
{"desp":"数据库操作失败","status":-1}
```

```json
{"desp":"planId参数格式非法","status":-2}
```

```json
{"desp":"删除成功","status":1}
```

#### 

#### 查看计划详情√

请求内部实现：PlanDao-findById

请求路径：/api/plan/detail

请求方法：get

请求参数：

| 名称 | 说明           | 数据类型 |
| ---- | -------------- | -------- |
| id   | 要查看计划的id | int      |

返回计划的详情，一条计划的内容如下：

| 名称        | 说明                                     | 数据类型 |
| ----------- | ---------------------------------------- | -------- |
| create_time | 该计划创建的时间                         | string   |
| desp        | 该计划的描述                             | string   |
| title       | 计划的标题                               | string   |
| icon        | 计划的图标编号                           | int      |
| deadline    | 计划的deadline（精确到天，格式20190709） | string   |
| flag_finish | 该计划是否已经完成                       | bool     |
| id          | 该计划的id                               | int      |
| finish_time | 该计划完成的时间                         | string   |

返回成功示例：

```json
{"desp":"999","create_time":"20190526233732","icon":999,"id":4,"deadline":"20200103","title":"999","flag_finish":false,"finish_time":""}
```

返回失败示例：

```json
{"desp":"不存在该id对应的计划","status":-8}
```

————我是分割线啦——————



接口

今日全部的习惯 未完成的习惯（合并）

任意时间 早 中 晚		添加时间排序

图标有编号

打卡接口+日志  取消打卡，今天这个习惯的日志清除

打卡时间精确到秒

20字，禁止换行，  全空格判空

添加习惯

名字 图标 颜色 时间 固定（一周周几） 提醒时间？？   自动弹出打卡日志

时间：早 中 晚 任意

所有的习惯、按时间分类    滑一次查一次  按创建时间排序  共计坚持天数

习惯详情

编辑习惯

习惯

图标 颜色 今日完成bool 连续天数  时间分类 名字 id号

坚持天数？？？习惯页和详情页有