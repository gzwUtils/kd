package com.gzw.kd.learn.writes;

/**
 * 项目名称：spring-demo
 * 类 名 称：AopDemo
 * 类 描 述：learn
 * 创建时间：2021/6/1 12:22 上午
 *
 * @author gzw
 */
@SuppressWarnings("all")
public class Demo {
    /**
     * jdk 动态代理 invocationHandler Method proxy  使用目标对象的类加载器  classLoader
     *  目标对象所实现的接口 也是反射获取的
     *  newProxyInstance 创建代理对象
     *
     *
     * cglib 通过继承目标类  实现它的子类 在子类中重写父类中同名的方法 实现功能的修改 （目标类不能是final ）实现methodInterceptor接口  重写intercept
     *  Enhancer.create()方法创建代理对象 newInstance
     *
     * 红黑树
     * 根节点为黑色
     * 从任意节点到其每个叶子节点的所以路径都包含相同的黑色节点
     * 每个红色节点的俩个子节点都是黑色
     *  变色/旋转
     *  把插入红色节点的俩个父节点变为黑色节点  并把它们的爷爷节点变为红节点
     *  最长路径和最短路径不超过俩倍
     *
     * 双亲委培模型 bootStrap classLoader extend classloader app classloader  避免类信息被重复加载和程序的安全性，父加载器优先子加载器加载类型到jvm，子加载器无法加载父加载器已经加载到jvm中的类型信息。
     * 果一个类加载器需要加载类，那么首先它会把这个类请求委派给父类加载器去完成，每一层都是如此。一直递归到顶层，当父加载器无法完成这个请求时，子类才会尝试去加载。这里的双亲其实就指的是父类，没有mother。父类也不是我们平日所说的那种继承关系，只是调用逻辑是这样
     * 类加载机制  加载 连接 初始化
     *
     */

    /**
     * java 虚拟机里的同步是基于进入和退出monitor对象来实现的，monitorentor monitorexit  同步代码块 监视器锁(monitor)  jvm调用操作系统的互斥原语来实现  被阻塞的线程会被挂起
     * 同步方法-由方法调用指令读取运行时常量池中的表结构的ACC_SYNCHRONIZED标志来隐式实现的 标志符
     * cas unsafe.compareAndSwap
     *
     */

    /**
     * cglib是通过动态字节码来生成目标类的子类；
     * 类加载 app  bootstrap ext
     *
     * Spring IOC  AOP
     * spring 是一个框架 ，在我们的整个开发流程中，所有的框架生产几乎都依赖与spring spring帮我们起到了一个IOC容器的作用，用来承载整体的bean对象
     * 他帮我们进行了整个对象从创建到销毁的整个生命周期的管理
     * 配置文件 注解
     * BeanFactory-classPathResource-BeanDefinitionReader-loadBeanDefinitions-BeanDefinitionRegistry；
     * spring 启动时，调用refresh();->configurableListableBeanFactory--映射BeanDefinition内容；
     *  invokeBeanFactoryPostProcessor   修改BeanDefinition内容；->finishBeanFactoryInit  实例化
     * 实例化 createBeanInstance 通过反射的方式创建对象 在堆里边开辟一个内存空间，可以通过反射或者CGLIB动态字节码生成来初始化相应的bean实例
     * BeanPostProcessor    BeanFactoryPostProcessor  后置处理器
     * 填充属性  populateBean( getSingleton()  doGetBean-doCreateBean-createBeanInstance(构照器-newInstance())-populateBean)  原型bean  直接doCreateBean
     * 执行aware接口方法[实现aware接口，获取任意的applicationContext容器对象，即为了使某些自定义对象能够方便的获取到容器对象](ApplicationContextAware、BeanNameAware )  ->执行before-init-method-after 方法  BeanPostProcessor（增强BeanAop）
     * Aop jdk  cglib  动态代理
     *
     * aop 动态代理 cglib  jdk 横向切面 切入点  asm 动态生成class文件  expression 表达式  methodIntercept
     * 面向切面编程- 责任链模式  AbstractAutoProxyCreator--methodIntecepor
     * beanFactory  factoryBean 事务传播  aop 三级缓存  redis
     * 三级缓存 ->（DefaultSingle） 为了延迟代理对象的创建  因为 Spring 的设计原则是在 Bean 初始化完成之后才为其创建代理 (BeanCurrentlyInCreationException) singletonObjects  earlySingletonObjects  singletonFactories
     * getSingleton()-   objectFactory.getObject(); createBean-  createBeanInstance   newInstance
     *
     * mybatis prepareStatement 预编译 持久层框架
     *
     * enableAutoConfiguration  AutoConfigurationImportSelector   帮助我们从类路径下的meta-info的spring.factories下加载一些spring自动配置的组件
     * spring事务的本质是数据库对事务的支持， 获取连接 开始事务  crud  commit /rollback 关闭连接
     * Spring是基于aop实现的 TransactionAspectSupport currentTransactionStatus()  @Transactional(rollbackFor = Exception.class)
     * 数据库隔离级别 可重复读 提交读 串行化
     * spring默认的事务传播行为  required support  threadLocal
     */


    /**
     * autowired 按类型type自动装配 允许null 属性required springframerworker 需要搭配@Qualifitied
     * resource 按照名称进行自动装配 j2ee自带的 有 type name 属性
     */

    /**
     * 线程池
     *
     * ThreadPoolTaskExecutor
     *
     * 核心线程数  最大线程数 队列大小 存活时间  线程名前缀 拒绝策略(new ThreadPoolExecutor.CallerRunsPolicy())
     *
     * cpu密集 以计算为主的程序运行过程中对CPU的占用率很高   等待时间几乎为0  尽量压榨CPU 线程池数为cpu数+1  也就是 为每一个线程分配一个cpu
     * io密集 大量的网络和文件操作的程序，这样的程序大多数的 时间都是CPU在等在I/O的读写操作，对CPU的使用率不高。这时一个线程处于等待的时候，另一个还可以在CPU里跑。
     * 所以线程池大小可以设为=2倍的CPU数
     *
     * 创建线程池时不允许使用 Executors 去创建，阿里巴巴开发手册上强制说明了：
     * 定长线程池 会在队列中堆积大量的请求 允许请求队列长度为Integer.max.value 产生oom
     * 缓存线程池 允许创建线程数量为Integer.max.value 产生oom
     *
     * 当调用shutdownNow()方法时，如果确定可能会有阻塞的任务存在，一定要捕获异常进行处理
     * pool.awaitTermination(2, TimeUnit.SECONDS)
     * 第一个参数为时间，第二个参数为单位
     *
     * 这是一个阻塞方法，返回true和false，线程池关闭为true，未关闭为false
     *
     * 在指定时间内，如果线程池关闭了，此方法结束阻塞，返回true，继续执行之后的代码
     *
     * 在指定时间内，如果线程池未关闭，会一直阻塞，直到指定时间到了返回false。
     *
     * private final AtomicInteger ctl=new AtomicInteger();
     *
     * 原子Integer值 4 个字节 32位 前3位表示线程运行状态 后29位表示线程数
     *
     * workQueue.offer(command) 加入阻塞队列
     *
     * workerCountOf(…) 计算线程数
     *
     * addWorker(…)方法 ctl.get()先判断运行线程数是否小于核心线程数 小于就通过CAS 将当前线程数ctl+1 跳出循环 开始创建worker 对象
     * 里面会创建一个线程，一个 Worker 对象就有一个线程。判断这个线程是否可启动，可以就将这个 Worker （其实就是线程）加入到 HashSet 集合中（也就是 workers ）
     * Worker 继承AQS 实现runable接口  重写run方法
     * ReenTrantLock
     */

    /**
     * volatile (修改共享变量)
     * 可见行 将缓存行的数据写会系统内存 使得其他cpu里缓存里该内存地址的数据失效
     * 多处理器条件下通过嗅探在总线上传播的数据来检查缓存的数据是否过期
     * 有序性  防止指令重排;
     * synchronized
     *
     * CountDownLatch  await方法阻塞当前线程。
     *
     * AQS state 状态  exclusiveOwnerThread 独占线程  阻塞等待队列 （waitStatus） fifo 默认是非公平锁。
     * lock - getState是否为0 、是否独占线程为当前线程（可重入）--否则放入等待队列中。
     * unlock ->release-tryRelease（成功就去唤醒等待队列的头节点）getState-1 并且判断当前线程是否为独占线程。最后判断 state 是否为0 设置独占线程为null  设置state为getState的状态。
     *
     * ReentrantLock 默认为非公平锁。
     */

    /**
     * rocketMq nameServer broker produce consumer
     *
     * nameServer 担任路由消息提供者
     * RocketMq采用文件系统存储消息，并采用顺序写写入消息，使用零拷贝发送消息，极大得保证了RocketMq的性能
     */

    /**
     * IOc容器的实例化初始化过程 refresh()  AbstractApplicationContext
     *
     * DefaultResourceLoader
     *
     */

    /** mybatis -->SqlSessionFactory、SqlSession、MapperProxy
     * mybatis-spring sqlSessionFactoryBean implement InitializingBean FactoryBean ApplicationListener  SqlSessionFactoryBuilder
     *
     * MapperFactoryBean-SqlSessionDaoSupport
     *
     * mybatis中很重要的调用链上，一个sqlSession包含一个executor，一个executor包含一个transaction，这个transanction是真正提供jdbc的connection的，这里负责创建transaction的是spirng提供的
     */

    /**
     * BlockingQueue
     *
     *
     * ConcurrentLinkedQueue
     */


    /**
     * 1、缓存雪崩、缓存穿透、缓存击穿
     * 【缓存雪崩】：指缓存同一时间大面积的失效，所以，后面的请求都会落到数据库上，
     * 造成数据库短时间内承受大量请求而崩掉。
     * 造成原因：例如，Radis系统第一次启动，缓存中没有数据；或者Radis挂掉导致数据范围丢失。
     * 解决方案：
     * · 随机设计缓存数据的过期时间，防止同一时间过期现象的发生；（常用）
     * · 给每一个缓存数据增加相应的缓存标记，记录缓存是否失效，如果缓存标记失效，则更新数据数据(浪费内存空间)
     * · 缓存预热(我们写一个接口，在启动之前，把数据先放到缓存中去，再去启动)（常用）
     * · 互斥锁(当缓存中没有数据去查数据库时，我们给这个键添加一把互斥锁，把数据放到缓存后，再释放这个锁)
     *
     * 【缓存穿透】：指缓存和数据库中都没有的数据，导致所有的请求都落在了数据库上，
     * 造成数据库短时间内承受大量请求而崩溃。
     * 造成原因：大多数场景是来自攻击；商城系统并发量很大时也会出现这种情况。
     * 解决方案：
     * · 采用布隆过滤器，将所有可能存在的数据哈希到一个足够大的bitmap中，一个一定不存在的数据会被这个
     *   bitmap拦截掉，从而避免了对底层存储系统的查询压力。（常用）
     * · 接口层增加校验，如用户鉴权校验，id做基础校验，id<=0的直接拦截；
     * · 从缓存中取不到的数据，在数据库中也没有取到，这是也可以将key-value对写为key-null,缓存有效时间
     *   可以设置短点，如30秒(设置太长会导致正常情况也没法使用)。这样可以防止攻击用户反复用同一个id暴力攻击。
     *
     * 【缓存击穿】：指缓存中没有但数据库中有的数据(一般是缓存时间到期)，这是由于并发用户特别多，同时读
     *  缓存没读到数据，又同时去数据库去取数据，引起数据库压力瞬间增大，造成过大压力。和缓存雪崩不同的是，
     * 缓存击穿并发查同一条数据，缓存雪崩是不同数据都过期了，很多数据都查不到从而查数据库。
     * 解决方案：
     * · 设置热点数据永远不过期。
     * · 加互斥锁。(其它线程可以进行cas自旋查库，把数据更新回来)
     *
     * 2、索引的基本原理
     * 索引用来快速地寻找那些具有特定值的记录。如果没有索引，一般来说执行查询时遍历整张表。
     * 索引的原理：就是把无序的数据变成有序的查询。
     * · 把创建了索引的列的内容进行排序
     * · 对排序排序结果生成倒排表
     * · 在倒排表内容上拼上数据地址链
     * · 在查询的时候，先拿到倒排表内容，再取出数据地址链，从而拿到具体数据
     *
     * 3、索引设置的原则
     * 查询更快、占用空间更小
     *  1>表的主键、外键必须有索引（一般设计中不使用外键）；
     *  2>数据量超过300的表应该有索引；
     *  3>经常与其他表进行连接的表，在连接字段上应该建立索引；
     *  4>经常出现在Where子句中的字段，特别是大表的字段，应该建立索引；
     *  5>索引应该建在选择性高的字段上，例如性别选择性很低，不适合建索引；
     *  6>索引应该建在小字段上，对于大的文本字段甚至超长字段，不要建索引；
     *  7>复合索引的建立需要进行仔细分析；尽量考虑用单字段索引代替；
     *
     *
     * 4、索引类别
     *  1>Primary Key(聚集索引)：InnoDB存储引擎的表会存在主键(唯一非null),如果建表的时候没有指定
     *    主键，则会使用第一非空的唯一索引作为聚集索引，否则InnoDB会自动帮你创建一个不可见的、长度
     *    为6字节的row_id用来作为聚集索引。 例：学号
     *
     *  2>Unique(唯一索引)：索引列的值必须唯一，但允许由空值(null:代表未知，所以不破坏唯一性)。若是
     *    组合索引，则列值的组合必须唯一。主键索引是一种特殊的唯一索引，不允许有空值。 例：身份证号
     *
     *  3>单列索引：一个索引只包含单个列
     *
     *  4>组合索引：在表的多个字段组合上创建的索引，只有在查询条件中使用了这些字段的左边字段时，索引
     *    才会被使用。使用组合索引时遵循最左前缀原则。
     *
     *  5>Key(普通索引)：是MySQL中基本索引类型，允许在定义索引的列中插入重复值和空值。
     *
     *  6>FULLTEXT(全文索引)：在定义索引的列上支持值的全文查找，允许重复值和空值，全文索引可以在CHAR
     *    、VARCHAR或者TEXT类型的列上创建。
     *
     *  7>SPATIAL(空间索引)：对空间数据类型的字段建立的索引。
     *
     *
     * 4、如何检测MySQL中建立的索引是否生效
     * 在select语句前加上explain就可以了：
     * EXPLAIN SELECT surname,first_name form a,b WHERE a.id=b.id
     * 索引失效的场景：
     * · 如果条件中有or
     * · like查询是以%开头
     * · 对于多列索引，不是使用的第一部分(第一个)，则不会使用索引
     * · 如果列类型是字符串，那一定要在条件中将数据使用引号引用起来,否则不使用索引
     *
     *
     * 6、事务管理（ACID）
     * 原子性（Atomicity）
     * 原子性是指事务是一个不可分割的工作单位，事务中的操作要么都发生，要么都不发生。
     * 一致性（Consistency）
     * 事务前后数据的完整性必须保持一致。
     * 隔离性（Isolation）
     * 事务的隔离性是多个用户并发访问数据库时，数据库为每一个用户开启的事务，不能被其他事务的操作数据所干扰，多个并发事务之间要相互隔离。
     * 持久性（Durability）
     * 持久性是指一个事务一旦被提交，它对数据库中数据的改变就是永久性的，接下来即使数据库发生故障也不应该对其有任何影响
     *
     *
     * 5、脏读、幻读、不可重复读
     * · 脏读：所谓脏读是指一个事务中访问到了另外一个事务未提交的数据
     * · 幻读：一个事务查询2次，得到的记录条数不一致(幻读是不可重复读的一种特殊场景)
     * · 不可重复读：一个事务查询同一条记录2次，得到的结果不一致
     * MySQL 里有四个隔离级别：
     * Read uncommttied（可以读取未提交数据）
     * Read committed（可以读取已提交数据）  解决了脏读的问题
     * Repeatable read（可重复读）  默认级别，每次读取的结果都一样，但是有可能产生幻读
     * Serializable（可串行化）。   会加锁，导致大量超时和锁竞争问题。
     * 在 InnoDB 中，默认为 Repeatable 级别，InnoDB 中使用一种被称为 next-key locking 的策略来避免幻读（phantom）现象的产生。
     * 隔离级别越高，越能保证数据的完整性和一致性，但是对并发性能的影响也越大。
     *
     *
     * 6、数据拆分前其实是要首先做准备工作的，然后才是开始数据拆分，我先讲拆分前需要做的事情：
     * 第一步：采用分布式缓存redis、memcached等降低对数据库的读操作。
     * 第二步：如果缓存使用过后，数据库访问量还是非常大，可以考虑数据库读、写分离原则。
     * 第三步：当我们使用读写分离、缓存后，数据库的压力还是很大的时候，这就需要使用到数据库拆分了。
     * 分库分表：
     * 垂直切分是指按照业务将表进行分类，分布到不同的数据库上面，这样也就将数据或者说压
     * 力分担到不同的库上面 。
     * 水平拆分：
     * 垂直拆分后遇到单机瓶颈，可以使用水平拆分。相对于垂直拆分的区别是：垂直拆分是把不同的表拆到不同的数据库中，而水平拆分是把同一个表拆到不同的数据库中。
     *
     * 7、SQL慢查询
     * 优化方向：
     * · 首先分析语句，看看是否load了额外的数据，可能是加载了许多结果中并不需要的的列，
     *   对语句进行分析以及重写
     * · 使用explan查看索引是否命中，修改语句或者索引
     * · 如果对语句的优化已经无法进行，可以考虑纵向分库分表。
     *
     *
     * 8、MySQL索引的数据结构，各自优劣
     * 索引的数据结构和具体存储引擎的实现有关，再MySQL中使用较多的索引有Hash索引、B+树索引，InnoDB存储
     * 引擎的默认索引实现为：B+树索引。对于哈希索引来说，底层的数据结构就是哈希表，因此在绝大多数需求为
     * 单条记录的时候，可以选择哈希索引，查询性能最快；其余大部分场景，建议选择B+树索引。
     *
     * 9、MySQL是不是实时刷新的，不是实时刷新，在查数据时为什么可以查到数据？？？？？？？
     *
     * 9、简述MyISAM和InnoDB的区别
     * 【MyISAM】适合做查询（MySQL在做读写分离的时候，读操作一般采用MyISAM，写操作使用InnoDB）
     * 不支持事务，但是每次查询都是原子的；
     * 支持表级锁，即每次操作操作是对整个表加锁；
     * 存储表的总行数；
     * 一个MYISAM表有三个文件：索引文件、表结构文件、数据文件；
     * 采用非聚集索引，索引文件的数据域存储指向数据文件的指针。辅索引不用保证唯一性。
     *
     * 【InnoDB】适合做写操作
     * 支持ACID的事务，支持事务的四种隔离界别；
     * 支持行级锁及外键约束：因此可以支持写并发；
     * 不存储总行数；
     * 一个InnoDb引擎存储在一个文件空间（共享表空间，表大小不受操作系统控制，一个表可能分布在多个文件里），
     * 也有可能为多个（设置为独立表空间，表大小受操作系统文件大小限制，一般为2G），受操作系统文件大小的
     * 限制。
     * 主键索引采用聚集索引（索引的数据域存储数据文件本身），辅索引的数据域存储主键的值；因此从辅索引查找
     * 数据，需要先通过辅索引找到主键值，再访问辅索引；做好使用自增主键，防止插入数据时，为维持B+树结构，
     * 文件的大调整。
     *
     * 总结：
     * InnoDB
     * 优点：支持事务，支持外键，并发量较大，适合大量 update，支持范围查询(双向链表)
     * 缺点：查询数据相对较慢，不适合大量的 select。
     *
     * MyISAM
     * 优点：查询数据相对较快，适合大量的 select，可以全文索引。
     * 缺点：不支持事务，不支持外键，并发量较小，不适合大量 update。
     *
     * 注：可以在my.ini文件中找到default-storage-engine=INNODB；
     *
     * 10、判断链表有环的三种方法
     * 方法一：首先从头节点开始，依次遍历单链表的每一个节点，每遍历到一个新节点，就从
     * 头节点重新遍历新节点之前的所有节点，用新节点ID和此节点之前所有节点ID依次作比较。
     * 如果发现新节点之前的所有节点当中存在相同节点ID，则说明该节点被遍历过两次，链表
     * 有环；如果之前的所有节点当中不存在相同的节点，就继续遍历下一个新节点，继续重复
     * 刚才的操作。
     *
     * 方法二：首先创建一个以节点ID为键的HashSet集合，用来存储曾经遍历过的节点。然后同
     * 样是从头节点开始，依次遍历单链表的每一个节点。每遍历到一个新节点，就用新节点和
     * HashSet集合当中存储的节点作比较，如果发现HashSet当中存在相同节点ID，则说明链表
     * 有环，如果HashSet当中不存在相同的节点ID，就把这个新节点ID存入HashSet，之后进入
     * 下一节点，继续重复刚才的操作。
     * 这个方法在流程上和方法一类似，本质的区别是使用了HashSet作为额外的缓存。
     *
     *
     * 方法三：首先创建两个指针1和2（在java里就是两个对象引用），同时指向这个链表的头
     * 节点。然后开始一个大循环，在循环体中，让指针1每次向下移动一个节点，让指针2每次
     * 向下移动两个节点，然后比较两个指针指向的节点是否相同。如果相同，则判断出链表有
     * 环，如果不同，则继续下一次循环。
     *
     * 11、单点登录
     * 利用Redis共享Session,将用户信息保存在Redis中。
     * 出现问题：cookie是不能跨域访问的，但是在二级域名里是可以共享cookie的。
     * 这样就是我们的项目有了局限性，必须将多个系统的域名同一，作为二级域名，
     * 统一平台提供使用主域名。这样就可以实现cookie的单点登录了。
     *
     * 注(二级域名)：所谓的二级域名实际上就是一个一级域名下面的主机名。
     *
     * 12、Java引用类型(强引用、软引用、弱引用、虚引用)
     * 强引用：Object o=new Object(); // 强引用
     * 强引用是使用最普遍的引用。如果一个对象具有强引用，那垃圾回收器宁愿抛出OOM（OutOfMemoryError）也不会回收它。
     *
     * 软引用：使用SoftReference创建的引用，强度弱于强引用，被其引用的对象在内存不足的时候会被回收，
     * 不会产生内存溢出。
     * String str=new String("abc");   // 强引用
     * SoftReference<String> softRef=new SoftReference<String>(str);    // 软引用
     * str = null;     // 消除强引用，现在只剩下软引用与其关联，该String对象为软可达状态
     * str = softRef.get();  // 重新关联上强引用
     *
     * 弱引用：弱引用是使用WeakReference创建的引用，弱引用也是用来描述非必需对象的，它是比软引用更弱的引用类型。在发生GC时，只要发现弱引用，不管系统堆空间是否足够，都会将对象进行回收。
     *
     * 虚引用：虚引用是使用PhantomReference创建的引用，虚引用也称为幽灵引用或者幻影引用，是所有引用类型中最弱的一个。一个对象是否有虚引用的存在，完全不会对其生命周期构成影响，也无法通过虚引用获得一个对象实例。
     * 作用：虚引用作用在于跟踪垃圾回收过程，在对象被收集器回收时收到一个系统通知。
     *
     * 13、SpringBean生命周期、作用域
     *  1> 解析类得到BeanDefinition
     *  2> 如果有多个构造方法，则要推断构造方法
     *  3> 确定好构造方法后，进行实例化得到一个对象
     *  4> 对对象中的加了@Autowired注解的属性进行属性填充
     *  5> 回调Aware方法，比如BeanNameAware,BeanFactoryAware  (使得bean可以收到spring通知)
     *  6> 调用BeanPostProcessor的初始化前的方法     (在Spring容器中完成bean实例化、配置以及其他初始化方法前后要添加一些自己逻辑处理。)
     *  7> 调用初始化方法
     *  8> 调用BeanPostProcessor初始化后的方法，在这里会进行AOP
     *  9> 如果当前创建的bean是单例的则会把bean放入到单例池
     *  10> 使用bean
     *  11> Spring容器关闭时调用DisposableBean中destory()方法。
     *
     * 作用域：
     * · singleton：默认，每个容器只有一个bean的实例，单例的模式由BeanFactory自身来维护。生命周期是与SpringIOC
     *   容器一致的(但在第一次被注入时才会创建)。
     * · prototype：原型模式，为每一个bean请求提供一个实例。在每次注入时都会创建一个新的对象。
     * · request：bean被定义为在每个HTTP请求中创建一个单例对象，也就是说单个请求中都会复用这一个单例对象。
     * · session：与request范围类似，确保每个session中有一个bean的实例，在session过期后，bean会随之失效。
     * · application：bean被定义在ServletContext的生命周期中复用一个单例对象。
     * · websocket：作用于一次 websocket 通信，若连接被释放，则 bean 自然也会被销毁。
     * · globalSession：与session大体相同，但仅在portlet应用中使用。Portlet规范定义了全局session的概念。
     *
     *
     * 14、Volitile
     * 是Java虚拟机提供的轻量级的同步机制，三大特性：
     * · 保证可见性 (线程A感知不到线程B操作了值的变化，使用Volitile关键字)
     * · 不保证原子性  (使用JUC的原子类，java.util.concurrent.atomic 底层用的CAS)
     * · 禁止指令重排 (通过插入内存屏障，禁止在内存屏障前后的指令执行)
     * 注：在执行程序时，为了提高性能，编译器和处理器常常会对指令做重排序，可能出现执行顺序错乱。
     *
     * 15、Java对象的组成
     * · 对象头：
     * 	·对象自身运行时的数据(如哈希码、锁状态标志、线程持有的锁等)
     * 	·类型指针(指向它的类元数据的指针，这个对象是哪个类的实例)
     * · 实例数据：真正存储的有效信息，也是在程序代码中所定义的各种类型的字段内容
     * · 对齐填充：起着占位符的作用(对象的大小必须是8字节的整数倍,当对象实例数据部分没有对齐时，就
     *   需要通过对齐填充来补全)
     *
     * 16、双亲委派机制
     * JAVA的类加载器：AppClassloader -> ExtClassloader -> BootStrapClassloader
     * 	· BootStrap Classloader 加载java基础类，这个属性不能在Java指令中指定，推断不是由Java语
     * 	  言处理；
     * 	· Extention Classloader 加载JAVA_HOME/ext下的jar包(日志包、数据库连接使用包等)。可通过
     * 	  -D java.ext.dirs另行指定目录
     * 	· AppClassloader 加载CLASSPATH，应用下的Jar包，可通过-D java.class.path另行指定目录。
     * 注：每种类加载都有它自己的加载目录都是有一个缓存的。
     * 双亲委派：向上委托查找，向下委托加载。
     * 作用：保护Java底层的类不会被应用程序覆盖。
     *
     *
     * 16、Java类的加载(通过双亲委派机制将Java的字节码数据加载到JVM内存当中，并映射成JVM认可的数据结构)
     * 类加载的过程主要分为三个部分：
     * ·加载：把class字节码文件从各个来源通过类加载器装载入内存中
     * 	·验证：为了保证加载进来的字节流符合虚拟机规范，不会造成安全错误。
     * 	·准备：主要是为类变量(注意:不是实例变量)分配内存，并且赋予初值。例如:引用类型的初值设为null
     * 	·解析：将常量池内的符号引用(标识)替换为直接引用(可以理解为一个内存地址)的过程。
     * ·初始化：这个阶段主要是对类变量初始化，是执行类构造器的过程。
     *
     *
     *
     * 17、SpringBoot 配置文件加载位置与优先级(优先级从高到低排序)
     * 当前项目目录下的一个/config子目录
     * 当前项目目录
     * 项目的resources即一个classpath下的/config包
     * 项目的resources即classpath根路径（root）
     *
     * 18、请求到达先经过过滤器(Filter),再到拦截器(Interceptor)
     *
     * 19、SpringBoot中文乱码问题
     * 1> properties文件中文乱码：在IDEA的File Encodings中进行配置
     * 2> SpringBoot编码导致的中文乱码：
     *    在application.properties文件中加上各方面的编码配置：
     *    banner.charset=UTF-8
     *    spring.messages.encoding=UTF-8
     *    server.tomcat.uri-encoding=UTF-8
     *    spring.http.encoding.charset=utf-8
     *    spring.http.encoding.enabled=true
     *    spring.http.encoding.force=true
     * 3> Controller返回普通字符串时，中文乱码
     *    返回 json 数据是不会有中文乱码问题的,但是，有时候会返回普通的String字符串,解决办法：
     * 		·在@RequestMapping(value = "/testString")标签中加上属性：produces="text/plain;charset=UTF-8"
     * 		 变成这样：@RequestMapping(value = "/testString",produces="text/plain;charset=UTF-8")
     * 		·另一种解决办法是继承 WebMvcConfigurerAdapter 类并复写相关方法，配置编码格式。
     * 注：直接返回json数据，这样请求方使用更加方便，这种方式更加符合规范，并且json格式数据是不会出现中文乱 码问题的。
     *
     * 20、ThreadLocal作用、场景、原理
     * 是什么？
     * 在JDK1.2的版本中就提供java.lang.ThreadLocal，ThreadLocal为解决多线程程序的并发问题提供了一种新的思路。
     * ThreadLocal并不是一个Thread，而是Thread的局部变量，也许把它命名为ThreadLocalVariable更容易让人理解一些。
     *
     * 作用：
     * 通过为每个线程提供一个独立的变量副本解决了变量并发访问的冲突问题。
     * 在很多情况下，ThreadLocal比直接使用synchronized同步机制解决线程安全问题更简单，更方便，且结果程序拥有更高的并发性。
     *
     * 应用场景：
     * 通常会使用synchronized来保证同一时刻只有一个线程对共享变量进行操作。这种情况下可以将类变量放到ThreadLocal类型的对象中，使变量在每个线程中都有独立拷贝，不会出现一个线程读取变量时而被另一个线程修改的现象。
     * 最常见的ThreadLocal使用场景为用来解决数据库连接、Session管理等。
     *
     * 21、切面失效策略
     *
     * 22、spring事务的实现方式和原理以及隔离级别？
     * 在使用Spring框架时，可以有两种使用事务的方式，一种是编程式的，一种是声明式的，@Transactional
     * 注解就是声明式的。
     *
     * 首先，事务这个概念是数据库层面的，Spring只是基于数据库中的事务进行了扩展，以及提供了一些能
     * 让程序员更加方便操作事务的方式。
     *
     * 比如我们可以通过在某个方法上添加@Transactional注解，就可以开启事务，这个方法中所有的sql都会
     * 在一个事务中执行，统一成功或失败。
     *
     * 在一个方法上加了@Transactional注解后，Spring会基于这个类生成一个代理对象，会将这个代理对象
     * 作为一个bean，当在使用这个代理对象的方法时，如果这个方法上存在@Transactional注解，那么代理
     * 逻辑会先把事务的自动提交设置为false，然后再去执行原本的业务逻辑方法，如果执行逻辑方法没有出
     * 出现异常，那么代理逻辑中就会将事务提交，入库出现了异常，那么则会将事务进行回滚。
     *
     * 当然，针对那些异常回滚事务是可以配置的，可以利用@Transactional注解中的rollbackFor属性进行配
     * 置默认情况下会对RuntimeException和Error进行回滚。
     *
     * spring事务隔离级别就是数据库的隔离级别：外加一个默认级别
     * · read uncommitted(读取未提交)
     * · read committed(读取已提交)
     * · repeatabel read(可重复读)
     * · serializable(可串行化)
     *
     * 22、乐观锁、悲观锁
     * 乐观锁和悲观锁是两种思想，用于解决并发场景下的数据竞争问题。
     * · 乐观锁：乐观锁再操作数据时非常乐观，认为别人不会同时修改数据。因此乐观锁不会上锁，只是在
     *   执行更新的时候判断一下在此期间别人是否修改了数据：如果别人修改了数据则放弃操作，否则执行
     *   操作。(在Java中java.util.concurrent.atomic包下面的原子变量类就是使用了乐观锁的一种实现方
     *   式CAS实现的。)
     *
     * · 悲观锁：在操作数据时比较悲观，认为别人会同时修改数据，因此操作数据时直接把数据锁住，直到
     *   操作完成后才会释放锁；上锁期间其他人不能修改数据。(Java中synchronized和ReetrantLock等独占
     *   锁就是悲观锁思想的实现。)
     * 22.1 两种锁的使用场景
     * 乐观锁适用于写比较少的情况下(多读场景)。省去了锁的开销，加大了系统吞吐量。
     * 悲观锁适用于多写场景下。
     * 22.2 乐观锁常见的两种实现方式(版本号控制或CAS算法实现)
     * · 版本号控制：一般是在数据表中加上一个数据版本号version字段，表示数据被修改的次数，当数据被
     *   修改时，version值会加一。当线程A要更新数据值时，在读取数据的同时也会读取version值，在提交
     *   更新时，若刚才读取到的version值为当前数据库中的version值相等时才更新，否则重试更新操作，
     *   直到更新成功。
     * · CAS算法：即compare and swap(比较与交换)，是一种有名的无锁算法。无所编程，即不使用锁的情况
     *   下实现多线程之间的变量同步，也就是在没有线程被阻塞的情况下实现变量的同步，所以也叫非阻塞
     *   同步。CAS算法涉及到三个操作数：
     * 	· 需要读写的内存值V
     * 	· 进行比较的值A
     * 	· 拟写入的新值B
     * 	当且仅当V的值等于A时，CAS通过原子方式用新值B来更新V的值，否则不会执行任何操作(比较和替
     * 	换是一个原子操作)。一般情况下是一个自旋操作，即不断重试。
     * 22.3乐观锁缺点：
     * 	· ABA问题(值A可能被改为其他值，然后又改回A)
     * 		解决方案：JDK1.5以后的AtomicStampedReference类就提供了此种功能，其中的compareAndSet
     * 		方法就是首先检查当前引用是否等于预期引用，并且当前标志是否等于预期标志，如果全部相
     * 		等，则以原子方式将该引用和该标志的值设置为给定的更新值。
     * 	· 循环时间长开销大(自旋CAS，也就是不成功就一直循环执行直到成功)
     * 	· 只能保证一个共享变量的原子操作
     * 		解决方案：JDK1.5开始，提供了AtomicRefrence类来保证引用对象之间的原子性，可以把多个
     * 		变量放在一个对象里来进行CAS操作。(利用AtomicRefrence把多个共享变量合并成一个共享变
     * 		量来操作)
     *
     * 23、spring是什么？
     * 轻量级的开源的J2EE框架。它是一个容器框架，用来装JavaBean(java对象)，中间层框架(万能胶)可以
     * 起一个连接作用，比如说把MyBatis和SpringMVC粘合在一起运用，可以让我们的企业开发更快、更简洁。
     * spring是一个轻量级的控制反转(IOC)和面向切面(AOP)的容器框架。
     * 	· 从大小与开销两方面而言Spring都是轻量级的
     * 	· 通过控制反转(IOC)的技术达到松耦合的目的
     * 	· 提供了面向切面编程的丰富支持，允许通过分离应用的业务逻辑与系统级服务进行内聚性的开发
     * 	· 包含并管理应用对象(Bean)的配置和生命周期，这个意义上是一个容器
     * 	· 将简单的组件配置、组合成为复杂的应用，这个意义上是一个框架
     *
     * 24、谈谈你对IOC的理解(容器概念、控制反转、依赖注入)
     * · IOC容器：实际上就是个map(key,value)，里面存的是各种对象(在xml里配置的bean节点、@repository
     *   、@service、@controller、@component)，在项目启动的时候会读取配置文件里面的bean节点，根据
     *   全限定类名使用反射创建对象放到map里，扫描到打上上述注解的类还是通过反射创建对象放到map里
     *   。
     *   这个时候map里就有这种对象了，接下来我们在代码里需要用到里面的对象时，再通过DI注入(autowired、
     *   resource等注解，xml里bean节点内的ref属性，项目启动的时候会读取xml节点的ref属性根据id注入
     *   ，也会扫描这些注解，根据类型或id注入；id就是对象名)。
     *
     * · 控制反转：没有引入IOC容器之前，对象A依赖于对象B，那么对象A在初始化或者运行到某一点的时候
     *   ，自己必须主动去创建对象B或者使用已经创建好的对象B。无论是创建还是使用B，控制权都在自己手
     *   上。
     *   引入IOC容器之后，对象A与对象B之间失去了直接联系，当对象A运行到需要创建对象B的时候，IOC容
     *   器会主动创建一个对象B注入到对象A需要的地方。
     *   通过前后的对比，不难看出来：对象A获得依赖对象B的过程，由主动行为变为了被动行为，控制权颠
     *   倒过来了了，这就是"控制反转"这个名称的由来。
     *   全部对象的控制权全部上缴给第三方IOC容器，所以，IOC容器成了整个系统的关键核心，它起到一种
     *   类似粘合剂的作用，把系统中的所有对象粘合在一起发挥作用，如果没有这个粘合剂，对象与对象之
     *   间会彼此失去联系，这就是有人把IOC容器比喻成“粘合剂”的由来。
     *
     * · 依赖注入：获得依赖对象的过程被反转了。控制反转之后，获得依赖对象的过程由自身管理变为了由
     *   IOC容器主动注入。依赖注入是实现IOC的方法，就是由IOC容器在运行期间，动态地将某种依赖关系注
     *   入到对象之中。
     *
     * 25、SpringMVC工作流程
     * · 用户发送请求至前端控制器DispatcherServlet
     * · DispatcherServlet收到请求调用HandlerMapping处理器映射器
     * · 处理器映射器找到具体的处理器(可以根据xml配置、注解进行查找)生成处理器及处理器拦截器(如果
     *   有则生成)一并返回给Dispatcherservlet。
     * · DispathcerServlet调用HandlerAdapter处理器适配器
     * · HandlerAdapter经过适配调用具体的处理器(Controller，也叫后端控制器)
     * · Controller执行完成返回ModelAndView
     * · HandlerAdapter将controller执行结果ModelAndView返回给DispatherServlet
     * · DispatcherServlet将ModelAndView传给ViewReslover视图解析器
     * · ViewReslover解析后返回具体View
     * · DispatcherServlet解析后返回具体View
     * · DispatcherServlet根据View进行渲染视图(即将模型数据填充至视图中)
     * · DispatcherServlet响应用户。
     *
     * 22、spring 事务传递、事务管理器
     * · required(spring默认的事务传播方式)：如果当前没有事务，则自己创建一个事务，如果当前存在事
     *   务，则加入这个事务；
     * · supports：当前存在事务，则加入这个事务，如果当前没有事务，则以非事务方法执行；
     * · mandatory：当前存在事务，则加入该事务，当前不存在事务则抛出异常；
     * · requires_new：创建一个新事务，如果存在事务，则挂起该事务；
     * · not_supported：以非事务方式执行，如果当前存在事务，则挂起当前事务；
     * · never：不使用事务，如果当前事务存在，则抛出异常；
     * · nested：如果当前事务存在，则在嵌套事务中执行，否则reqired的操作一样(开启一个事务)
     *
     * 23、spring事务什么时候会失效？
     * spring事务的原理是AOP(AOP生成一个代理类去做)，进行了切面增强，那么失效的根本原因是这个AOP不
     * 起作用了！常见情况有如
     * 下几种：
     * 	· 发生自调用，类里面使用this调用本类的方法(this通常省略)，此刻这个this对象不是代理类，
     * 	  而是UserService对象本身！
     * 	  解决方法很简单：让那个this变成UserService的代理类即可！
     * 	· 方法不是public的
     *       @Transactional只能用于public的方法上，否则事务不会生效，如果要在非public方法上，可以
     *            开启AspectJ代理模式；
     *     · 数据库不支持事务(例如：Myisam数据库引擎)
     * 	· 没有被spring管理；
     * 	· 异常被吃掉，事务不会回滚(或者抛出的异常没有被定义，默认为RuntimeException)
     *
     *
     * 24、GC回收做了什么工作、GC拒绝策略是什么？
     *
     * 25、Vue用什么做的异步请求？
     * Axios
     *
     * 26、Redis五种数据结构？
     * · String字符串类型(是redis中最基本的数据类型，一个key对应一个value。)
     * 	· String类型是二进制安全的，意思是 redis 的 string 可以包含任何数据。如数字，字符串，jpg
     * 	  图片或者序列化的对象。
     * 	· 使用：get, set, del, incr, decr等
     * 	· 实战场景：
     * 		· 缓存： 经典使用场景，把常用信息，字符串，图片或者视频等信息放到redis中，redis作为
     * 		  缓存层，mysql做持久化层，降低mysql的读写压力。
     * 		· 计数器：redis是单线程模型，一个命令执行完才会执行下一个，同时数据可以一步落地到其
     * 		  他的数据源。
     * 	    · session：常见方案spring session + redis实现session共享。
     * · Hash(哈希，是一个Mapmap，指值本身又是一种键值对结构，如value={{field1,value1},......fiel
     *   dN,valueN}}
     * 	· 使用：所有hash的命令都是h开头的hget 、hset 、hdel 等
     * 	· 实战场景：
     * 		· 缓存： 能直观，相比string更节省空间，的维护缓存信息，如用户信息，视频信息等。
     * · 链表(List 说白了就是链表（redis 使用双端链表实现的 List），是有序的，value可以重复，可以
     *   通过下标取出对应的value值，左右两边都能进行插入和删除数据。)
     * 	· 使用技巧：
     * 		· lpush+lpop=Stack(栈)
     * 		· lpush+rpop=Queue（队列）
     * 		· lpush+ltrim=Capped Collection（有限集合）
     * 		· lpush+brpop=Message Queue（消息队列）
     * 	· timeline：例如微博的时间轴，有人发布微博，用lpush加入时间轴，展示新的列表信息。
     * · Set(集合 集合类型也是用来保存多个字符串的元素，但和列表不同的是集合中 1.不允许有重复的元
     *   素，2.集合中的元素是无序的，不能通过索引下标获取元素，3.支持集合间的操作，可以取多个集合
     *   取交集、并集、差集。)
     * 	· 使用：命令都是以s开头的  sset 、srem、scard、smembers、sismember
     * 	· 实战场景
     * 		· 标签（tag）,给用户添加标签，或者用户给消息添加标签，这样有同一标签或者类似标签的
     * 		  可以给推荐关注的事或者关注的人。
     * 	    · 点赞，或点踩，收藏等，可以放到set中实现
     * · zset(有序集合)
     * 有序集合和集合有着必然的联系，保留了集合不能有重复成员的特性，区别是，有序集合中的元素是可
     * 以排序的，它给每个元素设置一个分数，作为排序的依据。
     * 注：有序集合中的元素不可以重复，但是score 分数 可以重复，就和一个班里的同学学号不能重复，但
     *     考试成绩可以相同。
     * 	使用： 有序集合的命令都是 以  z  开头    zadd 、 zrange、 zscore
     * 	· 实战场景：
     * 		· 排行榜：有序集合经典使用场景。例如小说视频等网站需要对用户上传的小说视频做排行榜
     * 		  ，榜单可以按照用户关注数，更新时间，字数等打分，做排行。
     *
     *
     *
     * 26、单点登录模型 redis存储session 跨域问题、如果避免Session过期
     * 基于session集中存储的实现方案：
     * 	· 新增Filter，拦截请求，包装HttpServletRequest
     * 	· 改写getSession方法，从session存储中获取session数据，返回自定义的HttpSession实现
     * 	· 在生成新Session后，写入sesssionid到cookie中
     * redis过期时间、session过期时间问题：
     * 每次request请求都会刷新Session，使得Session的销毁时间成为用户最后一次操作+10分钟；
     * redis设置key的时候可以设置过期时间的，你每次更新session的时候把过期时间设置10分钟后就可以了
     * ，十分钟后用户访问session时候就过期了，session自然失效了。
     *
     * 登录过程：当客户端第一次发送请求后，服务器A将生成的sessionID放入redis中并通过响应头把sessionID
     * 放入客户端的cookie中，这样客户端、服务器A和redis中都会有一个相同的session，当客户端访问服务
     * 器B的时候因为客户端自己携带了一个session，那么服务器B就拿着客户端带来的sessionID去与redis中
     * 的session对比，一致则继续操作，不一致跳回登录页面。
     *
     * 跨域问题：
     * 	· 方式一：可以将多台服务器域名设置为二级域名(多个应用程序的名字)，但不能解决互联网行业
     * 	  中的跨域问题；
     * 	· 方式二：在Spring Boot项目中添加@CrossOrigin(origins = "http://localhost:8081")
     * 	· 方式三：自己写一个配置类，实现WebMvcConfigurer接口，重写addCorsMappings方法，配置一些
     * 	  访问策略。
     * 	· 方式三：使用JWT(JSON Web Token)来解决。服务端不保存Token(令牌)，JWT有签名机制，也就是
     * 	  说你把认证服务器的公钥配置在业务服务器，然后检验JWT是否为认证服务器签发的。如果确认了
     * 	  JWT确实为认证服务器签发，那么jwt的payload中携带的用户名、用户权限等信息都是可信任的。
     * 		· 过期时间问题：设置刷新时间和过期时间。比如刷新时间设置为2天，过期时间设置为2周；
     * 		  token 颁发2天内，请求接口时 token 有效、不刷新；token 颁发2天至2周内，请求接口时
     * 		  ，token 有效，但达到了刷新时间，刷新重新颁发 token，把老的 token 加入黑名单，避免
     * 		  一刷多的问题。token 颁发2周后，请求接口时，token 失效，直接拦截请求。
     *
     * 27、HashMap和HashTable区别？其底层实现是什么？
     * · 区别
     * 	· HashMap方法没有synchronize修饰，线程非安全，HashTable线程安全；
     * 	· HashMap允许key和value为null，而HashTable不允许。
     * · 底层实现：数组+链表
     * jdk8开始链表高度到8、数组长度超过64，链表转变成红黑树，元素以内部类Node节点存在。
     * 	· 计算key的hash值，二次hash然后对数组取模，对应到数组下标；
     * 	· 如果没有产生hash冲突(下标位置没有元素)，则直接创建Node存入数组
     * 	· 如果产生hash冲突，先进行equal比较，相同则取代该元素，不同，则判断链表高度插入链表，链
     * 	  表高度达到8，并且数组长度到64则转变为红黑树，长度低于6则将红黑树转回链表。
     * 	· key为null，存在下标0的位置
     * ·数组扩容(超过数组0.75扩两倍)
     *
     * 28、hashCode equals
     * · 如果两个对象相等，则hashcode一定也是相同的
     * · 两个对象相等，对两个对象分别调用equals方法都返回true
     * · 两个对象有相同的hashcode值，它们也不一定是相等的
     * · 因此，equals方法被覆盖时，则hashCode方法也必须被覆盖
     * · hashCode()的默认行为是对堆上的对象产生独特值。如果没有重写hashCode(),则该class的两个对象
     *   无论如何都不会相等(即使这两个对象指向相同的数据)。
     *
     * 28、HashSet如何存储
     * · HashSet的实现其实非常简单，它只是封装了一个HashMap对象来存储所有的集合对象，所有放入HashSet
     *   中的集合元素实际上由HashMap的key来保存，而HashMap的value则存储了一个PRESENT,它是一个静态
     *   的Object对象。
     * · 对于HashSet中保存的对象，请注意正确重写其equals和hashCode方法，以保证放入的对象的唯一性。
     *
     * 28、Java sort排序底层数据数据结构，冒泡排序时间复杂度
     *
     * 29、CAS是如何实现的
     *
     *
     * 33、BeanFactory和ApplicationContext有什么区别？
     * · ApplicationContext是BeanFactory的子接口；
     * · ApplicationContext提供了更完整的功能：
     * 	· 继承MessageSource，因此支持国际化
     * 	· 统一的资源文件访问方式
     * 	· 提供在监听器中注册bean的事件
     * 	· 同时加载了多个配置文件
     * 	· 载入多个(有继承关系)上下文，使得每一个上下文都专注于一个特定的层次，比如应用的web层。
     * · BeanFactory采用的是延迟加载形式来注入Bean的，即只有在使用到某个Bean时(getBean())，才对该
     *   bean进行加载实例化。这样，我们就不能发现一些存在的Spring的配置问题。如果Bean的某一个属性
     *   没有注入，BeanFactory加载后，直到第一次使用调用getBean方法才会抛出异常。
     * · ApplicationContext是在容器启动时，一次性创建了所有的Bean。这样，在容器启动时，我们可以发
     *   现Spring中存在的配置错误，这样有利于检查所依赖属性的注入。ApplicationContext启动后预载入
     *   所有的单实例Bean，通过预载入单实例bean，确保当你需要的时候，你就不用等待，因为它们已经创
     *   建好了。
     * · 相对于基本的BeanFactory，ApplicationContext唯一的不足是占用内存空间，当应用程序配置Bean较
     *   多时，程序启动慢
     * · BeanFactory通常以编程的方式被创建，ApplicationContext还能以声明的方式创建，如使用ContextLoader
     * · BeanFactory和ApplicationContext都支持BeanPostProcessor、BeanFactoryPostProcessor的使用，
     *   但两者之间的区别是：BeanFactory需要手动注册，而ApplicationContext则是自动注册。
     *
     * 34、Java序列化之排除被序列化字段(transient/静态变量)
     * · transient关键字(只能修饰变量)：这个字段的生命周期仅存于调用者的内存中，而不会写到磁盘里持
     *   久化；
     * · 静态变量：一个静态变量不管是否被transient修饰，均不能被序列化(只有堆内存会被序列化，所以
     *   静态变量天生不会被序列化。)
     * 注：对象的序列化可以通过实现两种接口来实现，若实现的是Serializable接口，则所有的序列化将会
     * 自动进行，若实现的是Externalizable接口，则没有任何东西可以自动序列化，需要在writeExternal方
     * 法中进行手工指定所要序列化的变量，这与是否被transient修饰无关。
     *
     * 35、AOP实现方式(静态代理和动态代理)
     * · 静态代理的代表为AspectJ；
     * 	· 所谓的静态代理，就是AOP框架会在编译阶段生成AOP代理类，因此也称为编译时增强。
     * 	· 编译阶段将Aspect织入Java字节码中，运行的时候就是经过增强之后的AOP对象，proceed方法就
     * 	  是回调执行被代理类中的方法。
     * · 动态代理则以Spring AOP为代表。
     * AOP框架不会去修改字节码，而是在内存中临时为方法生成一个AOP对象，这个AOP对象包含了目标对象的
     * 全部方法，并且在特定的切点做了增强处理，并回调原对象的方法。
     * Spring AOP中的动态代理，主要有两种方式：JDK动态代理和CGLIB动态代理。
     * 	· JDK动态代理通过“反射”来接收被代理的类，实现InvocationHandler接口(核心类：Proxy)，如果
     * 	  没有实现接口，那么Spring AOP会选择使用CGLIB来动态代理目标类。
     *     · CGLIB（Code Generation Library），是一个代码生成的类库，可以在运行时动态地生成某个类
     * 	  的子类。注意，CGLIB是通过继承的方式做的动态代理，因此如果某个类被标记为final，那么它
     * 	  是无法使用CGLIB做动态代理的。
     * 注：使用动态代理实质上就是调用时拦截对象方法，对方法进行改造、增强！
     *
     * 36、Redis过期策略
     * · 定时删除(对内存友好，对cpu不友好)
     * 	· 到时间点就把所有过期的键删除了。
     * · 惰性删除(对cpu友好，对内存不友好)
     * 	· 每次从键空间取键的时候，判断一下该键是否过期了，如过期就删除。
     * · 定期删除(折中)
     * 	· 每隔一段时间去删除过期键，限制删除的执行时长和频率。
     * 注：Redis采用定时删除+惰性删除，所以未必会被立即删除。
     *
     * 37、线程池常用参数
     * · corePoolSize：核心线程最大数量(线程池中常驻线程的最大数量。)；
     * · maximumPoolSize：线程池中运行最大线程数(包括核心线程和非核心线程)；
     * · keepAliveTime：线程池中空闲线程(仅适用于非核心线程)所能存活的最长时间。
     * 	· 当需要执行的任务很多，线程池的线程数大于核心池的大小时，keepAliveTime才起作用。
     * · unit：存活时间单位；
     * · workQueue：存放任务的阻塞队列；
     * · handler：线程池饱和策略。
     *
     * 38、什么时线程安全？
     * 当多个线程访问某个方法时，不管你通过怎样的调用方式或者说这些线程如何交替的执行，我们在主程
     * 序中不需要去做任何的同步，这个类的结果行为都是我们设想的正确行为，那么我们就可以说这个类是
     * 线程安全的。
     *
     * 34、spring框架中的单例Bean是线程安全的吗？不是线程安全的！
     * Spring中的Bean默认是单例模式的，框架并没有对bean进行多线程的封装处理。
     * 如果Bean是有状态的，那就需要开发人员自己来进行线程安全的保证，最简单的办法就是改变bean的作
     * 用域，把"singleton"改为"prototype"这样每次请求Bean就相当于是new Bean()这样就保证了线程的安
     * 全了。
     * · 有状态就是有数据存储功能
     * · 无状态就是不会保存数据 controller, service和dao层本身并不是线程安全的，如果只是调用里面的
     *   方法，而且多线程调用一个实例的方法，会在内存中复制变量，这是自己的线程的工作内存，是安全
     *   的。
     * Dao会操作数据库Connection，Connection是带有状态的，比如说数据库事务，Spring的事务管理器使用
     * ThreadLocal为不同线程维护了一套独立的connection副本，保证线程之间不会互相影响(Spring是如何
     * 保证事务获取同一个Connection的)。
     * 不要在bean中声明任何有状态的实例变量或类变量，如果必须如此，那么就使用ThreadLocal把变量变为
     * 线程私有的，如果bean的实例变量或类变量需要在多个线程之间共享，那么就只能使用synchronized、
     * lock、CAS等这些实现线程同步的方法了。
     *
     * 35、Springcloud微服务之间调用？
     * @FeignClient(name = "offerprpall",url = "http://10.75.5.10:19225/")
     *
     * 35、springboot自动配置原理
     *
     * 36、SpringCloud服务消费者：restTemplate和feignClient
     * 在SpringCloud微服务架构下，各个业务会被拆分为独立的微服务。那么我们如何解决服务间调用的问题
     * ，SpringCloud默认提供了两种方式：restTemplate和feignClient。
     * 两者的区别：
     * 	· restTemplate: 使用起来较为麻烦，需要自己指定ribbon的负载均衡，但参数较灵活，请求的路
     * 	  径可以使用程序灵活控制。
     * 	· feignClient: 使用简单，默认集成了ribbon负载均衡，无需自己配置，但参数不灵活，适用于api
     * 	  固定的接口。
     *
     * 38、redis事务
     * 事务介绍
     * · Redis的事务是通过MULTI,EXEC,DISCARD和WATCH这四个命令来完成。
     * · Redis的单个命令都是原子性的，所以这里确保事务性的对象是命令集合。
     * · Redis将命令集合序列化并确保处于一事务的命令集合连续且不被打断的执行。
     * · Redis不支持回滚的操作。
     * 命令介绍
     * · MULTI
     * 	注：用于标记事务块的开始。
     * 	Redis会将后续的命令逐个放入队列中，然后使用EXEC命令原子化地执行这个命令序列。
     * 	语法：MULTI
     * · EXEC
     * 	在一个事务中执行所有先前放入队列的命令，然后恢复正常的连接状态。
     * 	语法：EXEC
     * · DISCARD
     * 	清除所有先前在一个事务中放入队列的命令，然后恢复正常的连接状态
     * 	语法：DISCARD
     * · WATCH
     * 	当某个事务需要按条件执行时，就使用这个命令将给定的键设置为受监控的状态。
     * 	语法：WATHC key [key ...]
     * · UNWATCH
     * 	清除所有先前为一个事务监控的键。
     * 	语法：UNWATCH
     *
     * 39、PostMapping 传递参数(对象、字符串)
     *
     * 40、描述下HTTP和HTTPS的区别？
     * HTTP：是互联网上应用最为广泛的一种网络通信协议，基于TCP，可以使浏览器工作更为高效，减少网络。
     * HTTPS：是HTTP的加强版，可以认为是HTTP+SSL(Secure Socket Layer)。在HTTP的基础上增加了一系列
     * 	的安全机制。一方面保证数据传输安全，另一方面对访问者增加了验证机制。是目前现行架构下，
     * 	最为安全的解决方案。
     * 主要区别：
     *     · HTTP的连接是简单无状态的，HTTPS的数据传输是经过证书加密的，安全性更高。
     *     · HTTP是免费的，而HTTPS需要申请证书，而证书通常是需要收费的，并且费用一般不低。
     * 	· 它们的传输协议不同，所以它们使用的端口也是不一样的，HTTP默认是80端口，而HTTPS默认是
     * 	  443端口。
     * HTTPS的缺点：
     * 	· HTTPS的握手协议比较耗时，所以会影响服务的响应速度以及吞吐量。
     * 	· HTTPS也并不是完全安全的，它的证书体系其实并不是完全安全的。并且HTTPS在面对DDOS这样的
     * 	  攻击时，几乎起不到任何作用。
     * 	· 证书需要费钱，并且功能越强大的证书费用越高。
     *
     * 41、ArrayList介绍
     * 特点：
     * 	· ArrayList的底层数据结构是数组，所以查找遍历快，增删慢。
     * 	· ArrayList可随着元素的增长而自动扩容，正常扩容的话，每次扩容到原来的1.5倍。
     * 	· ArrayList的线程是不安全的。
     * 扩容：
     * 	· 创建ArrayList后容量为0，添加第一个元素后，容量变为10，此后若需要扩容，每次扩容到原来
     * 	  的1.5倍。
     * 	  每次扩容都是通过Arrays.copyOf(elementData, newCapacity) 这样的方式实现的。
     * 	· ArrayList还提供了其它构造方法
     * 		List<Person> list1 = new ArrayList<>(50); //指定元素个数创建List
     * 		· 当我们在写代码过程中，如果我们大概知道元素的个数，比如一个班级大概有40-50人，我们
     * 		  可以指定个数的方式去构造，以避免底层数据的多次拷贝，进而提高程序的性能。
     *
     * 42、ArrayList和LinkedList区别 及使用场景
     * · ArrayList: 基于动态数组，连续内存存储，适合下标访问(随机访问)，扩容机制：因为数组长度固定
     *   ，超出长度存数据时需要新建数组，然后将老数组的数据拷贝到新数组，如果不是尾部插入数据还会
     *   涉及到元素的移动(往后复制一份，插入新数据)，使用尾插法并指定初始容量可以极大提高性能、甚
     *   至超过LinkedList(需要创建大量的node对象)。
     * · LinkedList: 基于链表，可以存储在分散的内存中，适合做数据插入及删除操作，不适合做查询：需
     *   要逐一遍历。遍历LinkedList必须使用itrator不能使用for循环，因为每次for循环体内通过get(i)取
     *   得某一元素时都需要对list重新遍历，性能消耗极大。另外不要试图使用indexOf等返回元素索引，并
     *   利用其进行遍历，使用indexOf对list进行了遍历，当结果为空时会遍历整个列表。
     *
     * 使用场景：
     * · 如果应用程序对数据有较多的随机访问，ArrayList对象要优于LinkedList对象；
     * · 如果应用程序有更多的插入或者删除操作，较少的数据读取，LinkedList对象要优于ArrayList对象；
     * · 不过ArrayList的插入，删除操作也不一定比LinkedList慢，如果在List靠近末尾的地方插入，那么
     *   ArrayList只需要移动较少的数据，而LinkedList则需要一直查找到列表尾部，反而耗费较多时间，这
     *   时ArrayList就比LinkedList要快。
     *
     * 43、ArrayList和HashMap的扩容机制
     * · 通过源码分析可以看出，ArrayList构造时如果未指定容量，则会在第一次add元素时，设置默认容量
     *   大小为10，之后每次新增元素时都会判断当前size+1，是否大于当前容量，如果大于则进行扩容，也
     *   就是ArrayList是在容量满的时候才会进行扩容，并且每次通过Arrays.copyOf的方式，扩容为原数组
     *   的1.5倍，最大容量为Integer.MAX_VALUE。
     *
     * · HashMap(默认大小16，如果进行初始化的时候传入了自定义容量大小参数size，那么初始化的大小就
     *   是正好size的2的整数次方，比如传入10，就是16，传入30大小就是32)与ArrayList一样，都是在第一
     *   次添加元素时，才会进行真正的容量初始化，即使通过构造方法指定了容量，也是在put元素时才初始
     *   化的，HashMap不像ArrayList是在容量满的时候才进行扩容，而是通过一个加载因子的取值与容量相
     *   乘，如果当前已使用的容量达到了这个数值就进行两倍扩容，这也是为了保证尽量避免Hash冲突造成
     *   的链表过长，降低效率的问题。
     *
     * · HashTable初始size为11，扩容：newsize = olesize*2+1
     *
     * 42、循环依赖问题
     * · 两种方式注入依赖
     * 	· 构造器注入
     * 		· 构造器循环依赖是无法解决的(注：延迟加载，是可以解决循环依赖的，未深了解)。
     * 			在创建对象时，编译器报错：new ServiceA(new ServiceB(new ServiceA()...));
     * 	· setter方法
     * 注：我们循环依赖问题只要A的注入方式是setter且singleton，就不会有循环依赖问题。
     *
     * · 只有单例的bean会通过三级缓存提前暴露(二级缓存)来解决循环依赖的问题，而非单例的bean，每次
     *   从容器中获取都是一个新的对象，都会重新创建，所以非单例的bean是没有缓存的，不会将其放到三
     *   级缓存中。
     *
     * · 3个Map和四大方法，总体相关对象
     * 	· singletonObjects	singletonFactories	earlySingletonObjects
     * 	· getSingleton	doCreateBean	populateBean	addSingleton
     *
     * · 一级缓存(也叫单例池)singletonObjects：存放已经经历了完整生命周期的Bean对象；
     *   二级缓存：earlySingletonObjects，存放早期暴露出来的Bean对象，Bean的生命周期未结束(属性还
     *   未填充完)；
     *   三级缓存：Map<String, ObjectFactory<?>>singletonFactories，存放可以生成Bean的工厂。
     * 	· 三级缓存中存放的是lambda表达式对象(为什么使用lambda表达式？)：
     * 		· 回调机制，当需要给外部暴露对象的时候，使用lambda表达式的回调方式能够最终返回出一
     * 		  个唯一的最终版本的对象，而我们在使用过程中，是无法判断或者设置什么时候被调用的，
     * 		  所以当第一次被调用的时候直接通过lambda表达式生成即可。
     *
     * · 三级缓存放到哪了呢？
     * DefaultSingletonBeanRegistry是SingletonBean注册器的默认实现。
     * 主要是通过内部的几个map对象,来保存注册的Bean。
     * public calss DefalutSingletonBeanRegistry extends SimpleAliasRegistry implements
     * 	SingletonBeanRegistry{
     * 	private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);
     * 	private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);
     * 	private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);
     * 	...
     * }
     *
     * · 如果您将A类和B类的bean配置为相互注入，则Spring IoC容器会在运行时检测到此循环依赖引用，并
     *   抛出BeanCurrentlyInCreationException。
     *
     *
     * 【过程】：
     * · A创建过程中需要B，于是A将自己放到三级缓存里面(lambda表达式：
     *   addsingletonFactory(beanName,() -> getEarlyBeanReference(beanName, mbd, bean));),去实例化
     *   B；
     *
     * · B实例化的时候发现需要A，于是B先查一级缓存，没有，再查二级缓存，还是没有，再查三级缓存，找
     *   到A然后把三级缓存里面的这个A放到二级缓存里面，并删除三级缓存里面的A；
     *
     * · B顺利初始化完毕，将自己放到一级缓存里面(此时B里面的A依然是创建中状态)然后后来接着创建A，
     *   此时B已经创建结束，直接从一级缓存里面拿到B，然后完成创建，并将A自己放到一级缓存里面。
     *
     * debug源码的流程：
     * 1> 调用doGetBean()方法，想获取beanA，于是调用getSingleton()方法从缓存中查找beanA；
     * 2> 在getSingleton()方法中，从一级缓存中查找，没有，返回null;
     * 3> doGetBean()方法中获取到的beanA为null，于是走对应的处理逻辑，调用getSingleton()的重载方法
     *    (参数为ObjectFactory的)；
     * 4> 在getSingleton()方法中，先将beanA_name添加到一个集合中，用于标记该bean正在创建中。然后回
     *    调匿名内部类的createBean方法；
     * 5> 进入AbstractAutowireCapableBeanFactory#doCreateBean，先反射调用构造器创建beanA的实例，然
     *    后判断：是否为单例、是否提前暴露引用(对于单例一般为true)、是否正在创建中(即是否在第四轮
     *    的集合中)。判断为true则将beanA添加到三级缓存中；
     * 6> 对beanA进行属性填充(populateBean()方法)，此时检测到beanA依赖beanB，于是开始查找beanB；
     * 7> 调用doGetBean()方法，和上面beanA的过程一样，到缓存中查找beanB，没有则创建，然后给beanB填
     *    充属性；
     * 8> 此时beanB依赖于beanA，调用getSingleton()获取beanA，依次从一级、二级、三级缓存中找，此时
     *    从三级缓存中获取到beanA的创建工厂，通过创建工厂获取到singletonObject，此时这个singletonObjcet
     *    指向的就是上面在doCreateBean()方法中实例化的beanA；
     * 9> 这样beanB就获取到了beanA的依赖，于是beanB顺利完成实例化，并将beanA从三级缓存移动到二级缓
     *    存中；
     * 10> 随后beanA继续它的属性填充工作，此时也获取到了beanB，beanA也随之完成了创建，回到getSingleton()
     *     中继续执行下去，将beanA从二级缓存移动到一级缓存中。
     *
     * 注(二级缓存也可解决循环依赖)：
     * · Spring解决循环依赖的核心就是提前暴露对象，而提前暴露的对象就是放置于二级缓存中；
     * · Bean都已经实例化了，为什么还需要一个生产Bean的工厂呢？这里实际上是跟AOP有关，如果项目中不
     *   需要为Bean进行代理，那么这个Bean工厂就会直接返回一个开始实例化的对象，如果需要使用AOP进行
     *   代理，那么这个工厂就会发挥重要作用了(回调机制，当需要给外部暴露对象的时候，使用lambda表达
     *   式的回调方式能够最终返回出一个唯一的最终版本的对象，而我们在使用过程中，是无法判断或者设
     *   置什么时候被调用的，所以当第一次被调用的时候直接通过lambda表达式生成即可)；
     * · 因此，Spring一开始提前暴露的并不是实例化的Bean，而是将Bean包装起来的ObjectFactory，为什么
     *   要这么做呢？
     *   这实际上涉及到 AOP，如果创建的Bean是有代理的，那么注入的就应该是代理Bean，而不是原始的
     *   Bean。但是Spring一开始并不知道Bean是否会有循环依赖，通常情况下（没有循环依赖的情况下），
     *   Spring 都会在完成填充属性，并且执行完初始化方法之后再为其创建代理。但是，如果出现了循环依
     *   赖的话，Spring 就不得不为其提前创建代理对象，否则注入的就是一个原始对象，而不是代理对象。
     *   因此，这里就涉及到应该在哪里提前创建代理对象？Spring 的做法就是在 ObjectFactory 中去提前
     *   创建代理对象(会在 earlyProxyReferences 中记录已被代理的对象);它会执行getObject()方法来获
     *   取到Bean(lambda 回调)。
     * 注：通过上面的解析，我们可以知道 Spring 需要三级缓存的目的是为了在没有循环依赖的情况下，延
     *     迟代理对象的创建，使 Bean 的创建符合 Spring 的设计原则。
     * · 二级缓存
     * 	· 我们现在已经知道，第三级缓存的目的是为了延迟代理对象的创建，因为如果没有依赖循环的话
     * 	  ，那么就不需要为其提前创建代理，可以将它延迟到初始化完成之后再创建。
     * 	· 既然目的只是延迟的话，那么我们是不是可以不延迟创建，而是在实例化完成之后，就为其创建
     * 	  代理对象，这样我们就不需要第三级缓存了。因此，我们可以将addSingletonFactory()方法进行
     * 	  改造。
     * 		if (!this.singletonObjects.containsKey(beanName)) { // 判断一级缓存中不存在此对象
     * 		object o = singletonFactory.getObject(); // 直接从工厂中获取 Bean
     * 		this.earlySingletonObjects.put(beanName, o); // 添加至二级缓存中
     * 		this.registeredSingletons.add(beanName);
     * 		这样的话，每次实例化完 Bean 之后就直接去创建代理对象，并添加到二级缓存中。测试结果
     * 		是完全正常的，Spring 的初始化时间应该也是不会有太大的影响，因为如果Bean本身不需要代
     * 		理的话，是直接返回原始 Bean 的，并不需要走复杂的创建代理 Bean 的流程。
     * · 结论：如果 Spring 选择二级缓存来解决循环依赖的话，那么就意味着所有Bean都需要在实例化完成
     *   之后就立马为其创建代理，而 Spring 的设计原则是在 Bean 初始化完成之后才为其创建代理。所以
     *   ，Spring 选择了三级缓存。但是因为循环依赖的出现，导致了 Spring 不得不提前去创建代理，因为
     *   如果不提前创建代理对象，那么注入的就是原始对象，这样就会产生错误。
     *
     * 43、ReentranLock 与 Synchronized 的不同？
     * · ReentranLock需要手动加锁解锁，Synchronized会被JVM自动解锁，为了避免程序出现异常而无法正常
     *   解锁的情况，使用ReentrantLock 必须在 finally 控制块中进行解锁操作。
     * · ReentrantLock 相比 synchronized 的优势是可中断、公平锁、多个锁。这种情况下需要使用ReentrantLock。
     *
     * 44、synchronized底层实现原理？
     * · 基于对象的监视器(ObjectMonitor),我们在字节码文件里面可以看到，在同步方法执行前后，有两个
     *   指令，进入同步方法前monitorenter，方法执行完成后monitorexit；
     * · 对象都有一个监视器ObjectMonitor，这个监视器内部有很多属性，比如当前等待线程数、计数器、当
     *   前所属线程等；其中计数器属性就是用来记录是否已被线程占有，方法执行到monitorenter时，计数
     *   器+1，执行到monitorexit时，计数器-1，线程就是通过这个计数器来判断当前锁对象是否已被占用
     *   （0为未占用，此时可以获取锁）；
     * 注意：一个synchronize锁会有两个monitorexit，这是保证synchronize能一定释放锁的机制，一个是方
     * 法正常执行完释放，一个是执行过程发生异常时虚拟机释放。
     *
     *
     * 43、并发编程
     * 可重入锁？
     * · 可重入锁又名递归锁，是指在同一个线程在外层方法获取锁的时候，再进入该线程的内层方法会自动
     *   获取锁（前提锁对象得是同一个对象或者class），不会因为之前已经获取过还没释放而阻塞。Java中
     *   ReentrantLock和synchronized都是可重入锁，可重入锁的一个优点是可一定程度避免死锁。
     * 	· 可以想一下例子：当执行 child.doSomething 时，该线程获得 child 对象的锁，在 doSomething
     * 	  方法内执行doAnotherThing 时再次请求child对象的锁，因为synchronized 是重入锁，所以可以
     * 	  得到该锁，继续在 doAnotherThing 里执行父类的 doSomething 方法时第三次请求 child 对象
     * 	  的锁，同样可得到。如果不是重入锁的话，那这后面这两次请求锁将会被一直阻塞，从而导致死
     * 	  锁。
     * · 可重入锁实现原理？
     *   每一个锁关联一个线程持有者和计数器，当计数器为 0 时表示该锁没有被任何线程持有，那么任何线
     *   程都可能获得该锁而调用相应的方法；当某一线程请求成功后，JVM会记下锁的持有线程，并且将计数
     *   器置为 1；此时其它线程请求该锁，则必须等待；而该持有锁的线程如果再次请求这个锁，就可以再
     *   次拿到这个锁，同时计数器会递增；当线程退出同步代码块时，计数器会递减，如果计数器为 0，则
     *   释放该锁。
     *
     * · 锁阻塞与唤醒的方法：
     * 	· 方式1：使用Object中的wait()方法让线程等待，使用Object中的notify()方法唤醒线程；
     * 	· 方式2：使用JUC包中的Condition的await()方法让线程等待，使用signal()方法唤醒线程；
     * 	· 方式3：LockSupport类可以阻塞(park)当前线程以及唤醒(unpark)指定被阻塞的线程；
     * 		· 线程阻塞需要消耗凭证(permit),这个凭证最多只有1个。
     * 		· 当调用park方法时
     * 			如果有凭证，则会直接消耗掉这个凭证然后正常退出；
     * 			如果无凭证，就必须阻塞等待凭证可用；
     * 		· 而unpark则相反，它会增加一个凭证，但凭证最多只能有1个，累加无效。
     *
     * · 公平锁非公平锁
     * 	· 公平锁：是指多个线程按照申请锁的顺序来获取锁，类似排队打饭，先来后到。
     * 	· 非公平锁：是指多个线程获取锁的顺序并不是按照申请锁的顺序，有可能后申请的线程比先申请
     * 	  的线程优先获取到锁，在高并发的情况下，有可能会造成优先级反转或者饥饿现象。
     * 	· 并发包中的ReentrantLock的创建可以指定构造函数的boolean类型来得到公平锁或者非公平锁，
     * 	  默认是非公平所！
     * 	· 对于Synchronized而言，也是一种非公平锁。
     *
     * · 自旋锁(spinlock)	例如CAS比较并交换 就是自旋
     * 是指尝试获取锁的线程不会立即阻塞，而是采用循环的方式去尝试获取锁，这样的好处是减少线程上下
     * 文切换的消耗，缺点是循环会消耗CPU。
     *
     * · CAS(CompareAndSwap)
     * 比较当前工作内存中的值和主内存中的值，如果相同则执行规定操作，否则继续比较直到主内存和工作
     * 内存中的值一致为止。
     * CAS有三个操作数，内存值V，旧的预期值A，要修改的更新值B。且仅当预期值A和内存值V相同时，将内
     * 存值修改为B，否则什么都不做。
     * CAS缺点：
     * 	· 循环时间长开销很大
     * 		· 可以看到源码中存在一个do...while操作，如果CAS失败就会一直进行尝试。
     * 	· 只能保证一个共享变量的原子操作
     * 		· 当对一个共享变量执行操作时，我们可以使用循环CAS的方式来保证原子性操作，但是对多个
     * 		  共享变量操作时，循环CAS就无法保证操作的原子性，这时候就可以用锁来保证原子性。
     *
     * · AQS(AbstranctQueuedSynchronizer)抽象队列同步器
     * 	· 是什么？
     * 	  用来构建锁或者其它同步器组件的重量级基础框架及整个JUC体系的基石，通过内置的FIFO队列来
     * 	  完成资源获取线程的排队工作，并通过一个int类型变量表示持有锁的状态。
     * 	· 能干什么？
     * 	  抢到资源的线程直接使用处理业务逻辑，抢不到资源的必然涉及一种排队等候机制。抢占资源失
     * 	  败的线程继续去等待(类似于银行业务办理窗口都满了，暂时没有受理窗口的顾客只能去候客区排
     * 	  队等候)，但等候线程仍然保留获取锁的可能且获取锁流程仍在继续(候客区的顾客也在等着叫号
     * 	  ，轮到了再去受理窗口办理业务)。
     *
     * 	  既然说到了排队等候机制，那么就一定会有某种队列形成，这样的队列是什么数据结构呢？
     * 	  如果共享资源被占用，就需要一定的阻塞等待唤醒机制来保证锁分配。这个机制主要用的是CLH队
     * 	  列的变体实现的，将暂时获取不到锁的线程加入到队列中，这个队列就是AQS的抽象的表现。它将
     * 	  请求共享资源的线程封装成队列的节点(Node)，通过CAS、自旋以及LockSupport.park()的方式，
     * 	  维护state变量的状态，使并发达到同步的控制效果。
     *
     * 	· AQS使用一个volatile的int类型的成员变量来表示同步状态，通过内置的FIFO队列来完成资源获
     * 	  取的排队工作将每条要去抢占资源的线程封装成一个Node节点来实现锁的分配，通过CAS完成对
     * 	  State值的修改。
     *
     * 	· AQS自身：
     * 		有阻塞就需要排队，实现排队必然需要队列。
     * 		state变量+CLH变种的双端队列。
     *
     * 	· 内部类Node(Node类在AQS类的内部)
     * 		· Node的int变量(waitStatus)
     * 			等待区其它顾客(其它线程)的等待状态，队列中每个排队的个体就是一个Node；
     * 			Node = waitStatus + 前后指针指向。
     *
     * 	· 个人理解(以ReentrantLock为例)：
     * 	  ReentrantLock 实现了 Lock接口，lock.lock()、lock.unlock()方法底层调用的Sync类的对应方
     * 	  法；Sync类是ReentrantLock的内部类，继承了AbstractQueuedSynchronizer；Sync下又有FairSync
     * 	  (公平锁)、NonfairSync(非公平锁)；
     *
     * 	· 整个ReentrantLock的加锁过程，可分为三个阶段：
     * 		· 尝试加锁；
     * 		· 加锁失败，线程入队列；
     * 		· 线程入队列后，进入阻塞状态。
     *
     * 44、作为服务注册中心，Eureka比Zookeeper好在哪里？
     * · Zookeeper保证是CP
     * 当向注册中心查询服务列表时，我们可以容忍注册中心返回的是几分钟以前的注册信息，但不能接受服
     * 务直接down掉不可用。也就是说，服务注册功能对可用性的要求要高于一致性。但是zk会出现这样一种
     * 情况，当master节点因为网络故障与其它节点失去联系时，剩余节点会重新进行leader选举。问题在于
     * 选举leader的时间太长，30~120s，且选举期间整个zk集群都是不可用的，这就导致在选举期间注册服务
     * 瘫痪。在云部署的环境下，因为网络问题使得zk集群失去master节点是较大概率会发生的事件，虽然服
     * 务最终能够恢复，但是漫长事件的选举时间导致的注册长期不可用是不能容忍的。
     * · Eureka保证的是AP
     * Eureka看明白了这一点，因此在设计时就优先保证可用性。Eureka各个节点都是平等的，几个节点挂掉
     * 不会影响正常节点的工作，剩余的节点依然可以提供注册和查询服务。而Eureka的客户端在向某个Eureka
     * 注册时，如果发现连接失败，则会自动切换至其它节点，只要有一个Eureka还在，就能保住注册服务的
     * 可用性，只不过查到的信息可能不是最新的，除此之外，Eureka还有一种自我保护机制，如果在15分钟
     * 内超过85%的节点都没有正常的心跳，那么Eureka就认为客户端与注册中心出现了网络故障，此时会出现
     * 以下几种情况：
     * 	· Eureka不再从注册列表中移除因长时间没有收到心跳而应该过期的服务
     * 	· Eureka仍然能够接受新服务的注册和查询请求，但是不会被同步到其它节点上（即保证当前节点
     * 	  依然可用）
     * 	· 当网络稳定时，当前实例新的注册信息会被同步到其它节点中。
     * 因此，Eureka可以很好的应对因网络故障导致部分节点失去联系的情况，而不会像zookeeper那样使整个
     * 注册服务瘫痪。
     *
     * 45、ribbon是什么？
     * · Spring Cloud Ribbon是基于Netflix Ribbon实现的一套客户端负载均衡的工具。
     * · 在配置文件中列出LoadBalance(简称LB：负载均衡)后面所有的机器，Ribbon会自动的帮助你基于某种
     * 规则(如简单轮询，随机连接，权重等等)去连接这些机器。我们也很容易使用Ribbon实现自定义的负载
     * 均衡算法。
     * · 使用
     * 	· 在客户端(消费方)添加Euraka依赖(主启动类添加@EnableEureka注解)、ribbon依赖，在获取
     * 	  RestTemplate的配置类中添加@LoadBalanced注解。
     * 	· 将消费方的固定地址请求路径改为请求服务的名称
     * 		private static final String REST_URL_PREFIX = "http://SPRINGCLOUD-PROVIDER-DEPT";
     * 	· 更改其它其它负载均衡算法1
     * 		在自己的写的配置类中更改        * 		@Bean
     *        public IRule myRule(){
     * 			return new RandomRule();
     *        }
     * 	· 更改其它其它负载均衡算法2(在微服务启动的时候就能去加载我们自定义的Ribbon类)
     * 		在主配置类上添加RibbonClient(name="SPRINGCLOUD-PROVIDER-DEPT",configuratoin = WangYiRule.class)
     * 注：Ribbon和Eureka整合以后，客户端可以直接调用，不用关心IP地址和端口号。
     *
     * · 自定义负载均衡算法(重写IRule接口下的实现类，这些实现类都继承了AbstractLoadBalancerRule实
     *   现了IRule接口)。
     *   · 重写AbstractLoadBalancerRule抽象类中的setLoadBalancer、getLoadBalancer方法；
     *   · 自定义一个策略实现IRule接口，重写其中的setLoadBalancer、getLoadBalancer方法；
     *   · 其中的算法有：
     * 	· RoundRobinRule 轮询
     * 	· RandomRule 随机
     * 	· AvailabilityFilteringRule: 会先过滤掉，跳闸，访问故障的服务~，对剩下的进行轮询
     * 	· RetryRule: 会先按照轮询获取服务~，如果服务获取失败，则会在指定的时间内进行重试。
     *
     * 46、Feign
     * Feign 集成了Ribbon 也是做负载均衡的
     * Feign 是面向接口编程，写一个接口(在消费方定义的，通过这个总接口去调用服务)，在接口上添加
     * @FeignClient注解(指定服务名称)
     * 总结(使用feign的两步)：
     * 	· 写一个接口
     * 	· 添加@FeignClient
     *    @FeignClient(value = "SPRINGCLOUD-PROVIDER-DEPT")
     * 	public interface DeptClientService{
     *        @GetMapping("/dept/get/{id}")
     *        public Dept queryById(@PathVariable("id") Long id);
     *    }
     *
     * 消费方就像在本地调用一样(把上面定义的接口注入进来，直接调用)
     * @Autowired
     * private DeptClientService service;
     * @RequestMapping("/consumer/dept/add")
     * public boolean add(Dept dept){
     * 	return this.service.addDept();
     * }
     *
     * · 在编写接口的项目中的主启动类上添加@EnableFeignClients(basePackages={"com.wangyi.springcloud"})
     *   指定自己写的接口可以被扫描到。
     * · 在编写接口的项目中的主启动类上添加@ComponentScan("com.wangyi.springcloud"),保证自己缩写的
     *   东西被扫描到，最后把我们所写的注解变成调用的方法。
     *
     *
     * 注：调用微服务的两种方式
     * · 微服务名字 【ribbon】
     * · 接口加注解【feign】
     *
     * 注：Feign其实还是通过调用Ribbon实现的负载均衡，使用Feign进行远程调用。
     *
     * 注：接口也可以new，不过要重写接口中的方法。
     *
     * 47、MySQL中int(10) int(11) 区别。
     * 显示宽度与整数类型的取值范围是无关的。显示宽度只是指明mysql最大可能显示的数字个数，数值的位
     * 数小于指定的宽度时左侧用数字0来填充，如果插入了大于显示宽度的值，只要该值不超过该类型整数的
     * 取值范围，数值依然可以插入，而且能够显示出来。
     *
     * 48、浅谈第三方登录用户表结构设计方
     * · 我方用户表与第三方用户表同为一张表
     * 	· 在我方表(t_user)中添加一个标识，表示我方用户与微信用户唯一绑定的字段，添加一个
     * 	  wx_openid字段。（这样的话需要修改表）
     * · 我方用户表一张表、第三方用户表一张表
     * 	由于第一种方案如果对接额外的第三方需要不断的修改用户，以及原来的代码逻辑，对生产可能造
     * 	成不确定因素。做两张表(第三方用户表t_third_acount表字段可以设计为：
     * 	user_id: 对应t_user的用户id
     * 	third_unique_acount: 第三方唯一用户id
     * 	type: 表示第三方类型，这里规定1.代表微信，2.代表QQ 3.代表苹果
     * 	bind_flag: 表示是否绑定， 1绑定，2解绑
     * 	create_date: 绑定时间
     * 	)
     * 	· 这样设计的话，以后一般不需要修改表结构；但是新添加第三方用户对接时，还是避免不了需要
     * 	  对原来的代码逻辑做改动。
     * · 我方用户表一张表、第三方用户表多张表
     * 	基于第二种方案，第三方用户表使用了一个type字段来标识不同的第三方用户体系，通过不断的新
     * 	增不同的枚举来标识不同的第三方。所以可以去除这个字段，然后不同的第三方使用不同的表来标
     * 	识。
     *
     * 49、优化接口性能的七个建议：
     * · 数据量比较大，批量操作数据入库
     * · 耗时操作考虑异步处理
     * · 恰当的使用缓存
     * · 优化程序逻辑、代码
     * · SQL优化
     * · 压缩传输内容
     * · 考虑使用文件/MQ等其它方式暂存，异步再落地DB
     *
     * 50、一条SQL的执行过程
     * · 客户端发送一条查询语句给服务器
     * · 服务器先检查查询缓存，如果命中了缓存，则立刻返回存储在缓存中的结果。否则进入下一阶段。
     * · 服务器端进行SQL解析、预处理，再由优化器生成对应的执行计划。
     * · MySQL管局优化器生成的执行之花，再调用存储引擎的API来执行查询。
     * · 将结果返回给客户端。
     *
     * 51、mysql去除重复数据 重建表_mysql删除表中重复值
     * · 建表加索引：create table b(id int primary key,name char(10)) ;
     * · 插入数据，有重复的replace:  replace into b select * from a;
     * · 重命名：drop table a; rename table b to a;
     *
     * 52、String StringBuffer 和 StringBuilder的区别是什么？String 为什么是不可变的
     * [可变性]
     * 简单来说：String类中使用final关键字修饰字符数组来保存字符串，所以String对象是不可变的。
     * 补充：Java9之后，都改用了
     *
     * 53、死锁
     * · 是什么？
     * 死锁是两个或更多线程阻塞着等待其它处于死锁状态的线程所持有的锁。死锁通常发生在多个线程同时
     * 但以不同的顺序请求同一组锁的时候。
     * 例如：线程1锁住了A，然后尝试对B进行加锁，同时线程2已经锁住了B，接着尝试对A进行加锁，这时死
     * 锁就发生了。线程1永远到不到B，线程2也永远得不到A，并且它们永远也不会知道发生了这样的事情。
     *
     * · 如何定位
     * 使用jps -l 命令定位进程号
     * 使用jstack [进程号] 找到死锁查看
     *
     * · 避免死锁
     * 	· 加锁顺序
     * 	· 加锁时限
     * 	· 死锁检测
     * 		死锁检测是一个更好的死锁预防机制，它主要是针对那些不可能实现按序加锁并且锁超时也不
     * 		可行的场景。每当一个线程获得了锁，会在线程和锁相关的数据结构中（map、graph等等）将
     * 		其记下。除此之外，每当有线程请求锁，也需要记录在这个数据结构中。
     * 		当一个线程请求锁失败时，这个线程可以遍历锁的关系图看看是否有死锁发生。
     *
     * 		那么当检测出死锁时，这些线程该做些什么呢？
     * 			· 一个可行的做法是释放所有锁，回退，并且等待一段随机的时间后重试。
     * 			· 一个更好的方案是给这些线程设置优先级，让一个（或几个）线程回退，剩下的线程就
     * 			  像没发生死锁一样继续保持着它们需要的锁。如果赋予这些线程的优先级是固定不变的
     * 			  ，同一批线程总是会拥有更高的优先级。为避免这个问题，可以在死锁发生的时候设置
     * 			  随机的优先级。
     *
     * 54、启动线程方法start和run有什么区别？
     * 只有调用了start方法，才会表现出多线程的特性，不同线程的run方法里面的代码交替执行。如果只是
     * 调用run方法，那么代码还是同步执行的，必须等待一个线程的run方法里面的代码全部执行完毕之后，
     * 另外一个线程才可以执行其run方法里面的代码。
     *
     * 55、停止线程
     * · run方法执行完成，自然终止。
     * · 设置一个标志位，当标志位为true时执行，然后将标志位设置为false
     * · 使用stop
     * 	已经被作废，如果强制让线程停止有可能使一些请理性的工作得不到完成。另外一个原因是对锁定
     * 	的对象进行解锁，导致数据得不到同步的处理，出现数据不一致的问题。
     * · 使用interrupt
     * 	· 调用interrupt方法不会真正的结束线程，在当前线程中打上一个停止的标记。
     * 	· Thread类提供了interrupted方法测试当前线程是否中断，isInterrupted方法测试线程是否已经
     *    	  中断。
     * 		· t.isInterrupted方法是检查自己所写线程是否被打上停止标记，Thread.interrupted方法是
     * 		  检查主线程是否被打上停止标记。
     * 	· 使用方法：
     * 	  要停止的线程调用interrupt()方法，通过.isInterrupted()方法查看是否被做了标记，如果做了
     * 	  标记通过throw new InterruptedException();抛出异常的方式停止线程。
     *       注：如果线程中有sleep代码，不管是否进入到sleep的状态，如果调用了interrupt方法都会产生
     *       异常信息。
     *
     * 56、线程的优先级
     * · 通过serPriority方法可以设置线程的优先级。
     * · 高优先级的线程总是大部分先执行完，但不代表高优先级的线程全部执行完。
     * · 线程的优先级还有随机性，也就是说优先级高的线程不一定每一次都先执行完。
     *
     * 57、mySQL最左索引
     * 给A、B、C建立联合索引：
     * · 查询时索引键全部用上，即使顺序错乱也会生效
     * · 部分索引用上，只要有最左A索引出现，也会生效
     *
     * 58、Java中的异常体系
     * Java中的所有异常都来自顶级父类Throwable；
     * Throwable下有两个子类Exception和Error；
     * 	· Error时程序无法处理的错误(如OOM)，一旦出现这个错误，则程序将被迫停止运行。
     * 	· Exception不会导致程序停止，又分为两个部分RunTimeException运行时异常和CheckedExceptio
     * 	  n检查异常。RuntimeException常常发生在程序运行过程中，会导致程序当前线程执行失败；
     * 	  CheckedException常常发生在程序编译过程中，会导致程序编译不通过。
     * NoClassDefFoundError和ClassNotFoundException区别？
     * · NoClassDefFoundError是一种Error，Error在大多数情况下代表无法从程序中恢复的致命错误，产生
     *   的原因在于JVM或者ClassLoader在运行时类加载器在classpath下找不到需要的类定义(编译期是可以
     *   正常找到的)，这个时候虚拟机就会抛出NoClassDefFoundError，通常造成该Error的原因是打包过程
     *   中漏掉了部分类，或者jar包出现损坏或篡改，对应的Class在classpath中不可用等等原因。
     * · ClassNotFoundException是属于Exception的运行时异常，大多时可以从代码中恢复的异常类型，导致
     *   该异常的原因大多是因为使用Class.forName()方法动态的加载类信息，但是这个类在类路径中并没有
     *   被找到。
     *
     * 59、@RequestParam和@PathVariable这两者之间区别
     * · 用@RequestParam请求接口时,URL是:http://www.test.com/user/getUserById?userId=1
     * 	· 应用场景:这种方式应用也非常广，像CSDN或者是博客园都在用它，这里就不贴图了
     * · 用@PathVariable请求接口时,URL是:http://www.test.com/user/getUserById/2
     * 	· 主要应用场景是：不少应用为了实现RestFul的风格，采用@PathVariable这种方式。
     * 根据业务场景的需求决定使用其中一种或者是结合使用。不过它们都有一个共同点，那就是都是可见。
     *
     * 60、项目中使用log4j
     * · pom文件中引入 spring-boot-starter-logging、logstash-logback-encoder
     * · 在service类上添加@Slf4j注解
     * · 在业务类中，打印想看的信息：log.info("生成条款文件条款====" + clauseCode);
     *   在catch中打印一些错误信息：log.error("调用异常" + e);
     * 注：关于配置(如：配置最大存在时间60天，文件大小50M)
     *
     * 61、SpringBoot AOP应用场景
     * AOP 为我们提供了处理问题的全局化视角，使用得当可以极大提高编程效率。
     * · 使用AOP记录日志
     * 	如果要记录对控制器接口的访问日志，可以定义一个切面，切入点即为控制器中的接口方法，然后
     * 	然后通过前置通知来打印日志。
     * · 使用AOP监控性能
     * 	在原发项目的性能测试阶段或者项目部署后，我们会希望查看服务层方法执行的时间。以便精准的
     * 	了解项目中哪些服务方法执行速度慢，后续可以针对慢的服务进行性能优化。
     * 	Tips：正常情况下，用户查看页面或进行更新操作时，耗时超过1.5秒，就会感觉到明显的迟滞感。
     * 	由于前后端交互也需要耗时，按正态分布的话，大部分交互耗时在0.4秒左右。
     * · 使用AOP统一后端返回值格式
     * 	前后端分离的项目结构中，前端通过Ajax请求后端接口，此时最好使用统一的返回值格式供前端处
     * 	理。此处就可以借助AOP来实现正常情况、异常情况返回值的格式统一。
     *
     * 62、== 和 equals比较
     * · ==对比的是栈中的值，基本数据类型是变量值，引用类型是堆中内存对象的地址
     * · equals：Object中默认也是采用==比较，通常会重写。
     * 注：String默认以对equals进行了重写(拿出单个字符进行对比，是否一致)
     *
     * 63、final
     * · 修饰类：表示类不可被继承
     * · 修饰方法：表示方法不可被子类继承，但是可以重载
     * · 修饰变量：表示变量一旦被赋值就不可以更改它的值
     * (1)修饰成员变量
     * · 如果final修饰的是类变量(static修饰的)，在声明的时候需要赋值或者静态代码块中赋值；
     * · 如果final修饰的是成员变量(类中定义没被static修饰)，在声明的时候就需要赋值或者代码块中赋值
     *   或者构造器中赋值。
     * (2)修饰局部变量
     * · 局部变量只声明没有初始化，不会报错，与final无关；在使用之前一定要赋值；但是不允许第二次赋
     *   值；
     * (3)修饰基本数据类型和引用类型数据
     * · 基本数据类型：数值一旦在初始化之后便不能更改；
     * · 引用类型：初始化之后便不能再让其指向另一个对象。但是引用的值是可变的。
     *
     * 为什么局部内部类和匿名内部类只能访问局部final变量？
     * 编译之后会生成两个class文件。将局部变量设置为final使得局部变量与内部类内建立的拷贝保持一致
     *
     * 64、List和Set的区别
     * · List：有序，按对象进入的顺序保存对象，可重复，允许多个Null元素对象，可以使用Iterator取出
     *   所有元素再逐一遍历，还可以使用get(int index)获取指定下标的元素。
     * · Set：无序，不可重复，最多允许有一个Null元素对象，取元素时只能用Iterator接口取的所有元素，
     *   再逐一遍历各个元素。
     *
     * 65、可以在 Bean 的类上使用 @Scope 注解更改bean的作用域如：
     * @Scope(value = WebApplicationContext.SCOPE_REQUEST,
     *         proxyMode = ScopedProxyMode.INTERFACES)
     *
     * 66、回表
     * 如果select所需获得列中有大量的非索引列，索引就需要到表中找到相应的列的信息就叫回表。
     * InnoDB聚集索引的叶子节点存储行记录。
     * 基于非主键索引的查询需要多扫描一棵索引树，因此我们在应用中应该尽量使用主键查询。
     * 所谓回表查询：先定位主键值，再定位行记录，它的性能较扫一遍索引树更低。
     * 使用聚集索引（主键或第一个唯一索引）就不会回表，普通索引就会回表。
     * [索引覆盖]：只需要在一棵索引树上就能获取SQL所需的所有列数据，无需回表，速度更快。
     * [如何实现索引覆盖？]：将被查询的字段，建立到联合索引里去。
     *
     * 67、left join 多表查询 where 与 on 的区别？
     * 使用left join时on后面的条件只对右表有效
     * 	· on是在生成临时表的时候使用的条件，不管on的条件是否起到作用，都会返回左表 (table_name
     * 	  1)的行。
     * 	· where则是在生成临时表之后使用的条件，此时已经不管是否使用了left join了，只要条件不为
     * 	真的行，全部过滤掉。
     *
     * 68、八大数据结构(数组、栈、队列、链表、树、图、字典树、哈希表)
     * · 数组(一维数组、多维数组)
     * 	基本操作：insert、get、delete、size
     * · 栈(LIFO先进后出)
     * 	基本操作：Push、Pop、isEmpty、Top(返回顶部元素，但并不移除它)
     * · 队列(与栈相似,FIFO先进先出)	、
     * 	基本操作：Enqueue() —— 在队列尾部插入元素、Dequeue() ——移除队列头部的元素、
     * 			  isEmpty()——如果队列为空，则返回true、Top() ——返回队列的第一个元素
     * · 链表(链表一般用于实现文件系统、哈希表和邻接表。)
     * 	基本操作：InsertAtEnd、InsertAtHead、Delete、DeleteAtHead 、Search、isEmpty
     * · 图(图是一组以网络形式相互连接的节点。节点也称为顶点。 一对节点（x，y）称为边（edge）)
     * 	类型：无向图、有向图
     * 	程序语言中两种表示形式：邻接矩阵、邻接表；
     * 	常见图遍历算法：广度优先搜索、深度优先搜索；
     * · 树(树形结构被广泛应用于人工智能和复杂算法，它可以提供解决问题的有效存储机制。)
     * · 字典树(Trie)
     * 	字典树，也称为“前缀树”，是一种特殊的树状数据结构，对于解决字符串相关问题非常有效。
     * 	它能够提供快速检索，主要用于搜索字典中的单词，在搜索引擎中自动提供建议，甚至被用于IP的
     * 	路由。
     * 	以顶部到底部的方式存储，其中绿色节点“p”，“s”和“r”分别表示“top”，“thus”和“theirs”的底部。
     * · 散列表(哈希表)
     * 	哈希法（Hashing）是一个用于唯一标识对象并将每个对象存储在一些预先计算的唯一索引（称为“
     * 	键（key）”）中的过程。因此，对象以键值对的形式存储，这些键值对的集合被称为“字典”。可以
     * 	使用键搜索每个对象。基于哈希法有很多不同的数据结构，但最常用的数据结构是哈希表。
     *
     * 69、数据库建表规则
     * · 一对多时：把外键建在多的一方(多个学生记一个老师比一个老师记多个学生好记)
     * · 多对多时：建立一张中间表(如：用户权限关联表)
     *
     * 69、SpringBoot常见问题
     * 1> SpringBoot是否可以使用xml配置？
     * SpringBoot推荐使用Java配置而非XML配置，但是SpringBoot中也可以使用XML配置，通过@ImportResource
     * 注解可以引入一个xml配置。
     * 2> 核心配置文件是什么？
     * bootstrap.properties和application.properties
     * 有何区别？
     * 单纯做SpringBoot开发，可能不太容易遇到bootstrap.properties配置文件，但是再结合SpringCloud时
     * 这个配置就会经常遇到了，特别是在需要加载一些远程配置文件的时候。
     * 	· bootstrap由父ApplicationContext加载，比application优先加载，配置在应用程序上下文的引
     * 	  导阶段生效。一般来说我们在SpringCloud Config或者Nacos中会用到它。且bootstrap里面的属
     * 	  性不能被覆盖；
     * 	· application(.yml或者.peoperties)：由ApplicationContext加载，用于springboot项目的自动
     * 	  化配置。
     * 3> Spring Profiles允许用户根据配置文件(dev, test, prod等)来注册bean。因此，当应用程序在开发
     * 中运行时，只有某些bean可以加载。
     * 4> 配置运行端口：server.port = 8090
     * 5> 如何实现Spring Boot应用程序的安全性？
     * 为了实现SpringBoot的安全性，我们使用spring-boot-starter-security依赖项，并且必须添加安全配
     * 置。它只需要很少的代码。配置类将必须扩展。
     * WebSercurityConfigurerAdapter并覆盖其方法。
     * · Spring Security支持两种认证方式
     * 	· 通过form表单来认证
     * 	· 通过HttpBasic来认证
     * · Spring Security中提供了BCryptPasswordEncoder密码编码工具，可以非常方便的实现密码加密加盐
     *   相同铭文加密出来的结果总是不同，这样就不需要用户去额外保存盐的字段了，这一点比Shiro要方便
     *   很多。
     * · 登录配置
     *   对于登录接口，登录成功后的相应，登录失败后的响应，我们都可以在WebSecurityConfigurerAdapter
     *   的实现类中进行配置。
     * · 忽略拦截
     *   如果某一个请求地址不需要拦截的话，有两种方式实现：
     * 	· 设置该地址匿名访问
     * 	· 直接过滤掉该地址，即该地址不走Spring Security过滤器链
     * · 控制授权
     *   可以区分是不是登录者、区分角色信息(按钮展示)
     *
     * 70、inner join 与 left join(会生成临时表)之间的区别
     * · left join(左连接)	 返回包括左表中的所有记录和右表中连接字段相等的记录
     * · right join(右连接) 返回包括右表中的所有记录和左表中连接字段相等的记录
     * · inner join(等值连接) 只返回两个表中连接字段相等的行
     * on、where 区别？
     * · 对于left join, 不管on后面跟什么条件，左表的数据全部查出来，因此要想过滤需要把条件放到
     *   where后面；
     * · 对于inner join, 满足on后面的条件表的数据才能查出，可以起到过滤作用，也可以把条件放到
     *   where后面。
     * #和$的区别？ #可以防止SQL注入
     * #相当于对数据 加上 双引号，$相当于直接显示数据。
     * select * from user where name = #{name}
     * select * from user where name = 'csdn'
     *
     * select * from user where name=${name}
     * select * from user where name=csdn
     * select * from user where username=wang and password = 1 or 1=1
     * 注：MyBatis排序时使用order by 动态参数时需要注意，用$而不是#。
     *
     * 71、hashmap为什么用红黑二叉树而不用B+树
     * B+树在数据库中被应用的原因就是B+树比B树更加“矮胖”，B+树的非叶子结点不存储数据，所以每个结点
     * 能存储的关键字更多。所以B+树更能应对大量数据的情况。
     * jdk1.7中的HashMap本来是数组+链表的形式，链表由于其查找慢的特点，所以需要被查找效率更高的树
     * 结构来替换。
     * 如果用B+树的话，在数据量不是很多的情况下，数据都会“挤在”一个结点里面。这个时候遍历效率就退
     * 化成了链表。
     *
     * 72、为什么使用红黑树不适用平衡二叉树？
     * ·平衡二叉树
     * 	· 从任何一个节点出发，左右子树深度之差的绝对值不超过1。
     * 	· 左右子树仍然为平衡二叉树。
     *   · 如果在插入时破坏了平衡通过左右旋及进行调整
     *   · 删除：对于平衡二叉树来说，在最坏情况下，需要维护从被删节点到根节点这条路径上所有节点的
     *     平、衡性，旋转的量级是O(logN)。但是红黑树就不一样了，最多只需3次旋转就会重新平衡，旋转
     * 	的量级是O(1)。
     *   · 保持平衡：平衡二叉树高度平衡，这也就意味着在大量插入和删除节点的场景下，平衡二叉树为了
     *     保持平衡需要调整的频率会更高。
     * · 红黑树：
     * 	· 每个节点只有两种颜色：红色和黑色。
     * 	· 根节点是黑色的。
     * 	· 每个叶子节点（NIL）都是黑色的空节点。
     * 	· 从根节点到叶子节点，不会出现两个连续的红色节点。
     * 	· 从任何一个节点出发，到叶子节点，这条路径上都有相同数目的黑色节点。
     * 总结：和红黑树相比，AVL树是严格的平衡二叉树，平衡条件必须满足(所有结点的左右子树高度差不超
     * 过1)。不管我们是执行插入还是删除操作，只要不满足上面的条件，就要通过旋转来保存平衡，而因为
     * 旋转非常耗时，由此我们可以知道AVL树适合用于插入与删除次数比较少，但查找多的情况。
     * 平衡二叉树(AVL)局限性：
     * 由于维护这种高度平衡所付出的代价比从中获得的效率收益还大，故而实际的应用不多，更多的地方是
     * 用追求局部而不是非常严格整体平衡的红黑树。当然，如果应用场景中对插入删除不频繁，只是对查找
     * 要求较高，那么AVL还是较优于红黑树。
     */

    /**
     * show profiles;
     * show profile for query n; n  query-id
     *
     * explain
     * ID  序列号
     * select_type  查询类型
     * type 访问类型 range ref eq-ref const
     * possible_keys
     * key
     * key_len
     *
     *
     * 批量生成百万数据过程
     * SET GLOBAL log_bin_trust_function_creators=TRUE; --
     * DELIMITER $$   -- 写函数之前必须要写，该标志
     *
     * CREATE FUNCTION mock_data()		-- 创建函数（方法）
     * RETURNS INT 						-- 返回类型
     * BEGIN								-- 函数方法体开始
     * 	DECLARE num INT DEFAULT 1000000; 		-- 定义一个变量num为int类型。默认值为100 0000
     * 	DECLARE i INT DEFAULT 0;
     *
     * 	WHILE i < num DO 				-- 循环条件
     * 		 INSERT INTO app_user(`name`,`email`,`phone`,`gender`,`password`,`age`)
     * 		 VALUES(CONCAT('用户',i),'2548928007qq.com',CONCAT('18',FLOOR(RAND() * ((999999999 - 100000000) + 1000000000))),FLOOR(RAND()  *  2),UUID(),FLOOR(RAND()  *  100));
     * 		SET i =  i + 1;	-- i自增
     * 	END WHILE;		-- 循环结束
     * 	RETURN i;
     * END; 								-- 函数方法体结束
     *
     * SELECT mock_data(); -- 调用函数
     *
     *
     *
     *
     *
     *
     */

    /**
     *  postman
     * var timestamp = Math.round(new Date().getTime());
     * var body = pm.request.body.raw;
     * var sign = "timestamp=" + timestamp + "&token=token&body=" + body;
     * sign = CryptoJS.MD5(sign).toString();
     * pm.environment.set("sign", sign);
     * pm.environment.set("timestamp", timestamp);
     */

    /**
     * 意图识别 文本匹配算法
     *
     * 编辑距离
     * jaccard 相似度
     * word2vec
     */


    /**
     * 查询服务进程CPU情况：top –Hp pid
     * 查询JVM GC相关参数：jstat -gc pid 2000 (对 pid [进程号] 每隔 2s 输出一次日志)
     * 打印当前堆栈信息：jstack -l pid >> stack.log
     */


    /**
     * spring 事务的几个接口
     *
     * PlatformTransactionManager：（平台）事务管理器，Spring 事务策略的核心。
     * TransactionDefinition：事务定义信息(事务隔离级别、传播行为、超时、只读、回滚规则)。
     * TransactionStatus：事务运行状态。
     *
     *
     * AutoConfigurationImportSelector  加载自动装配 实现 importSelector 重写了selectImports方法 getAutoConfigurationEntry
     * @Configuration --- ConditionalOnClass
     */

}
