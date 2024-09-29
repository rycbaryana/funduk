#### Get all tasks

```http
GET /api/tasks
```
| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `tag`      | `string`    | **Optional**. FIlter by tag|
| `difficulty`| `string` | **Optional**. Filter by difficulty
| `sort`| `string` | **Optional**. Sort by something (title, difficulty)
| `order`| `string`|**Optional**  Sort order, either asc (ascending) or desc (descending) (default is asc)

#### Get task

```http
GET /api/tasks/${id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `int`    | **Required**. Id of the task to fetch|


#### Get user

```http
GET /api/users/${id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `int`    | **Required**. Id of the user to fetch|

#### Get user submissions

```http
GET /api/users/${id}/submissions
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `int`    | **Required**. Id of the user to fetch|
|`page`	|`int`|	**Optional**. Page number for pagination (default is 1).
|`size`| `int`| **Optional**. Number of submissions per page (default is 10).
