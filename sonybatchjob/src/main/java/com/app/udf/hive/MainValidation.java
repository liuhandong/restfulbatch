package com.app.udf.hive;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;


interface NumHelper {
    //获得a到b之间的随机数
    int nextAtoB(int a, int b);
}

class MyRandom {
    // 返回a到b之间的随机数，比如 a = 100,b == 200 返回100到200之间的随机数
    public int nextIntAtoB(int a, int b) {
        //创建随机数对象
        Random ran = new Random();
        int i = ran.nextInt(b - a + 1) + a;
        return i;
    }
}

@FunctionalInterface
interface IntCalculator{
    int calculate(int x, int y);
}

@FunctionalInterface
interface LongCalculator{
    long calculate(long x, long y);
}


public class MainValidation {
    static class Person {
        private String name;
        private String englishName;
        //private String age;
        //private String sex;
        private Long age; // Hope to live long
        private Boolean sex;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEnglishName() {
            return englishName;
        }

        public void setEnglishName(String englishName) {
            this.englishName = englishName;
        }

        public Long getAge() {
            return age;
        }

        public void setAge(Long age) {
            this.age = age;
        }

        public Boolean getSex() {
            return sex;
        }

        public void setSex(Boolean sex) {
            this.sex = sex;
        }
        /*
        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }
         */
    }

    private static final String delimiter = "\\|";

    /** 列表单层遍历修饰符 */
    private static final Function<Function, Consumer<List>>
            traversalListF = f -> l -> IntStream.range(0, l.size()).forEach(i -> l.set(i, f.apply(l.get(i))));
    /** 前置调用修饰符 */
    public static final Function<Function, Function<Consumer, Function>>
            beforeF = f -> c -> o -> { c.accept(o); return f.apply(o); };

    /** 函数幺元 */
    private static final Function identity = o -> o;

    /** 后置调用修饰符 */
    public static final Function<Function, Function<Consumer, Function>>
            afterF = f -> c -> o -> { Object oo = f.apply(o); c.accept(oo); return oo; };

    /** 映射单层遍历修饰符 */
    private static final Function<Function, Consumer<Map>>
            traversalMapF = f -> m -> m.forEach((k,v) -> m.put(k, f.apply(v)));

    /** 集合单层遍历修饰符 */
    private static final Function<Function, Consumer<Set>>
            traversalSetF = f -> s -> {
        List l = new ArrayList(s);
        traversalListF.apply(f).accept(l);
        s.clear();
        s.addAll(l);
    };

    /** 反射修改对象属性 */
    private static final Function<Function, Function<Object, Consumer<Field>>>
            modifyF = f -> o -> field -> {
        try {
            field.setAccessible(true);
            field.set(o, f.apply(field.get(o)));
        } catch (IllegalArgumentException | IllegalAccessException e){ /* pass.accept(o) */ }
    };

    /** 谓词: 属性在对象中是否static修饰 */
    private static final Predicate<Field> isStaticField = field -> Modifier.isStatic(field.getModifiers());

    /** 对象单层遍历器 */
    private static final Function<Function, Consumer>
            traversalObjectF = f -> o -> Stream.of(o.getClass().getDeclaredFields()).forEach(
            field -> {
                if (isStaticField.test(field))  ; /* pass.accept(field) */
                else                            modifyF.apply(f).apply(o).accept(field);
            });

    /** 谓词匹配修饰符 */
    public static final Function<Predicate, Function<Function, Function>>
            matchingF = p -> f -> o -> p.test(o) ? f.apply(o) : o; /* identity.apply(o) */

    /** 类型匹配修饰符 */
    public static final Function<Class, Function<Function, Function>>
            typeF = clazz -> matchingF.apply(o -> o.getClass().equals(clazz));

    public static void main(String[] args){
        // https://blog.csdn.net/coderlius/article/details/84745796
        //测试
        List<Integer> list = Arrays.asList(1, 2, 3);
        //traversalListF.apply(beforeF.apply(identity).apply(System.out::println)).accept(list);

        // 测试
        //traversalListF.apply(afterF.apply(beforeF.apply(x -> x).apply(System.out::println)).apply(System.out::println)).accept(list);

        // 测试
        /*
        Map<String, Integer> map = new HashMap();
        map.put("first", 1);
        map.put("second", 2);
        map.put("third", 3);
        traversalMapF.apply(beforeF.apply(identity).apply(System.out::println)).accept(map);

        //测试
        Set<Integer> set = new HashSet();
        set.add(1);
        set.add(2);
        set.add(3);
        traversalSetF.apply(x -> 1).accept(set);
        set.forEach(System.out::println);

        //测试
        Person myDaughter = new Person();
        myDaughter.setName("Xinyu");
        myDaughter.setEnglishName("Alice");
        myDaughter.setAge("1");
        myDaughter.setSex("Female");

        traversalObjectF.apply(beforeF.apply(identity).apply(System.out::println)).accept(myDaughter);
        */
        //测试
        Person myDaughter = new Person();
        myDaughter.setName("Xinyu");
        myDaughter.setEnglishName("Alice");
        myDaughter.setAge(1L);
        myDaughter.setSex(true);
        traversalObjectF.apply(afterF.apply(typeF.apply(String.class).apply(x -> "Hello, " + x)).apply(System.out::println)).accept(myDaughter);

        /*
        List<String> oneArray = new ArrayList<>();
        //oneArray.add("125");
        oneArray.add("43");
        oneArray.add("2");
        oneArray.add("9");
        oneArray.add("507");
        List<String> twoArray = new ArrayList<>();
        twoArray.add("2");
        twoArray.add("54");
        twoArray.add("43");
        twoArray.add("507");
        twoArray.add("9");
        //for(String s:new ArrayCompareUDF().evaluate(oneArray,twoArray)){
        //    System.out.println(s);
        //}
           */
        /*
        String str = "1|2|3";
        for(String s:str.split(delimiter)){
            System.out.println(s);
        }
        System.out.println(str.indexOf("oo"));

        engine((IntCalculator) ((x,y)-> x + y));
        engine((long x, long y)-> x * y);
        engine((int x,int y)-> x / y);
        engine((long x,long y)-> x % y);
        */

        /*
        //创建MyRandom对象
        MyRandom myRandom = new MyRandom();
        //在Lambda表达式中调用已经存在的方法
        NumHelper nh3 = (a,b)-> myRandom.nextIntAtoB(a,b);
        //获得400到500之间的随机数
        System.out.println(nh3.nextAtoB(400,500));

        //使用方法引用简化Lambda表达式
        NumHelper nh4 = myRandom::nextIntAtoB;
        //获得500到600之间的随机数
        System.out.println(nh4.nextAtoB(500,600));
*/
    }

    private static void engine(IntCalculator calculator){
        int x = 2, y = 4;
        int result = calculator.calculate(x,y);
        System.out.println(result);
    }
    private static void engine(LongCalculator calculator){
        long x = 2, y = 4;
        long result = calculator.calculate(x,y);
        System.out.println(result);
    }
}
