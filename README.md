# 事务线程池
  事务+线程池
### 目标：
1. 多个任务组成一个事务，这些任务可以是提前编排好的，也可以是未发生的。
2. 同一事务内的任务可支持随机和顺序执行。
3. 事务之间是隔离的，一个事务任务繁重不会影响其他的事务执行效率。
4. 支持任务的持久化，保证任务不会丢失。
5. 支持事务回滚。
6. 支持事务取消、支持设置事务的等待超时和执行超时时间。
