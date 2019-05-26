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

addPlan updatePlan finish cancelFinish delay findTodayPlan findAllPlan hasPermission isFinish findById deletePlan


## 接口设计

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

#### 查询今日需要完成的计划

请求内部逻辑：PlanDao-findTodayPlan

记得update之前过期的计划？

主关键字已完成未完成，第二关键字deadline，第三关键字创建时间

id，完成状态，图标，名字，描述

#### 查询全部计划

请求内部逻辑：PlanDao-findAllPlan

记得update之前过期的计划？

主关键字已完成未完成，第二关键字deadline，第三关键字创建时间

id，完成状态，deadline，图标，名字

#### 完成计划&取消完成计划

计划id

状态码

#### 添加计划&修改计划

计划名字 具体内容 日期选择 图标选择

#### 删除计划

计划id

状态码

#### 查看计划详情





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