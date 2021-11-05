package collections;

import common.Employee;
import common.Student;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static java.util.Collections.*;

public class CollectionsTutorial {
    public static void main(String[] args) {
        iterators();
        collectionsClass();
        arraysClass();
        objectsClass();
        comparableAndComparator();
        maps();
        sets();
        lists();
        queues();
    }

    private static void objectsClass() {

    }

    private static void comparableAndComparator() {
        Random ageGenerator = new Random();
        List<Employee> employees = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int age = ageGenerator.nextInt(100);
            employees.add(new Employee( i+"", age));
        }
        ageGenerator.nextInt();

        System.out.println("Before Sorting: " + employees);

        Collections.sort(employees);
        System.out.println("Sorted by name order using comparable: " + employees);

        // ALTERNATIVELY (employeeOne, employeeTwo) -> Integer.compare(employeeOne.getAge(), employeeTwo.getAge()) OR (employeeOne, employeeTwo) -> employeeOne.getAge() - employeeTwo.getAge();
        Comparator<Employee> customEmployeeComparator = Comparator.comparingInt(Employee::getAge);

        Collections.sort(employees, customEmployeeComparator);
        System.out.println("Sorted using custom comparator: " + employees);
    }

    private static void iterators() {
        //public interface Iterator<E>
        //public interface Spliterator<T>
        //public interface ListIterator<E> extends Iterator<E>

        List<Integer> list = Arrays.asList(1,2,3);

        // An iterator over a collection which allow the caller to remove elements from the underlying collection during the iteration with well-defined semantics.
        //hasNext() - Returns true if this list iterator has more elements
        //next() - Returns the next element in the list and advances the cursor position.
        //remove() - Removes from the underlying collection the last element returned by this iterator and can be called only once per call to next().
        //The behavior is unspecified if the underlying collection is modified while the iteration is in progress in any way other than by calling this method.
        Iterator<Integer> iterator = list.iterator();

        //An object for traversing and partitioning elements of a source. A Spliterator may traverse elements individually using tryAdvance() or sequentially in bulk using forEachRemaining().
        //It may also partition off some of its elements (using trySplit() as another Spliterator, to be used in possibly-parallel operations.
        //Operations using a Spliterator that cannot split, or does so in a highly imbalanced or inefficient manner, are unlikely to benefit from parallelism.
        //Traversal and splitting exhaust elements, each Spliterator is useful for only a single bulk computation.
        //Spliterator API was designed to support efficient parallel traversal in addition to sequential traversal, by supporting decomposition as well as single-element iteration.
        //In addition, the protocol for accessing elements via a Spliterator is designed to impose smaller per-element overhead than Iterator, and to avoid the inherent race involved in having separate methods for hasNext() and next().
        Spliterator<Integer> spliterator = list.spliterator();

        //An iterator for lists that allows the programmer to traverse the list in either direction, modify the list during iteration, and obtain the iterator's current position in the list.
        //It has no current element; its cursor position always lies between the element that would be returned by a call to previous() and the element that would be returned by a call to next().
        //hasNext() - Returns true if this list iterator has more elements
        //next() - Returns the next element in the list and advances the cursor position.
        //hasPrevious() - Returns {@code true} if this list iterator has more elements when traversing in reverse direction.
        //previous() - Returns the previous element in the list and moves the cursor position backwards.
        //nextIndex() - Returns the index of the element that would be returned by next(). Returns list size if the list iterator is at the end of the list.
        //previousIndex() - Returns the index of the element that would be returned by previous(). Returns -1 if the list iterator is at the beginning of the list.
        //
        //remove() - Removes from the list the last element that was returned by next() or previous().  This call can only be made once per call to next() or previous().
        //It can be made only if add() has not been called after the last call to next() or previous().
        //
        //set() - Replaces the last element returned by next() or previous() with the specified element.
        //This call can be made only if neither remove() or add() have been called after the last call to next() or previous().
        //
        //add() - Inserts the specified element into the list immediately before the element that would be returned by next(), if any, and after the element that would be returned by previous(), if any.
        //If the list contains no elements, the new element becomes the sole element on the list.
        //The new element is inserted before the implicit cursor: a subsequent call to next() would be unaffected, but previous() would return the new element.
        //It increases by one the value that would be returned by nextIndex() previousIndex()
        ListIterator<Integer> listIterator = list.listIterator();
    }

    private static void arraysClass() {
        System.out.println("------------------Arrays Methods---------------------");
        Integer[] arr = new Integer[10];
        //Assigns the specified Object reference to each element of the specified array
        Arrays.fill(arr, 1);
        systemOut(arr, "arr");

        //Copies the specified array, truncating or padding with nulls (if necessary)so the copy has the specified length and returns the new copy array
        arr = Arrays.copyOf(arr, 20);
        systemOut(arr, "arr");

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == null) arr[i] = i;
        }
        systemOut(arr, "arr");

        //Copies the specified range of the specified array into a new array. The initial index of the range from must lie between zero and original.length, inclusive.
        Integer[] arr2 = Arrays.copyOfRange(arr, 12,17);
        systemOut(arr2, "arr2");

        int key = 12;
        //Searches the specified array for the specified object using the binary search algorithm. The array must be sorted into ascending order.
        int result = Arrays.binarySearch(arr, key);
        System.out.println( "Key: " + key + " found at index: " + result);

        //Returns a fixed-size list backed by the specified array.  (Changes to the returned list "write through" to the array.)
        List<Integer> list = Arrays.asList(arr);
        systemOut(list.toArray(new Integer[list.size()]), "list");

        List<Integer> listTwo = Arrays.stream(arr).map(x-> x*2).collect(Collectors.toList());
        systemOut(listTwo.toArray(new Integer[listTwo.size()]), "listTwo");

        //Sorts the specified array of objects according to the order induced by the specified comparator.
        //All elements in the array must be mutually comparable by the specified comparator (that is, c.compare(e1, e2) must not throw a ClassCastException fro elements e1, e2 in array.
        //This sort is guaranteed to be stable equal elements will not be reordered as a result of the sort.
        Arrays.sort(arr, Comparator.reverseOrder());
        systemOut(arr, "arr");

        //Cumulates, in parallel, each element of the given array in place, using the supplied function.
        Arrays.parallelPrefix(arr, (a,b) -> a+b);
        systemOut(arr, "arr");

        //Set all elements of the specified array, using the provided generator function to compute each element.
        Arrays.setAll(arr, i-> i*5);
        systemOut(arr, "arr");
        System.out.println("---------------------------------------");
    }

    private static void systemOut(Integer[] arr, String arrName) {
        StringBuilder builder = new StringBuilder();
        builder.append(arrName).append(" = ");
        for (int i = 0; i < arr.length-1; i++) {
            builder.append(arr[i]+ ", ");
        }
        builder.append(arr[arr.length-1]);
        System.out.println(builder.toString());
    }

    private static void collectionsClass() {
        System.out.println("------------------Collections Methods---------------------");
        List<Integer> list = Arrays.asList(1,2,3,4,5,6,7,8);
        System.out.println(list);

        //Reverses the order of the elements in the specified list.
        reverse(list);
        System.out.println(list);

        //Sorts the specified list into ascending order, according to the Comparable of its elements.
        sort(list);
        System.out.println(list);

        //Searches the specified list for the specified object using the binary search algorithm.  The list must be sorted into ascending order.
        int key = 2;
        int result = binarySearch(list, 2);
        System.out.println( "Key: " + key + " found at index: " + result);

        //Sorts the specified list according to the order induced by the specified comparator. All elements in the list must be mutually comparable using the specified comparator.
        sort(list, (a,b) -> b.compareTo(a));    //sort(list, Comparator.reverseOrder())) works same
        System.out.println(list);

        shuffle(list);
        System.out.println(list);

        //Swaps the elements at the specified positions in the specified list.
        swap(list, 3 ,5);
        System.out.println(list);

        //Returns the minimum element of the given collection, according to the natural ordering of its elements.
        Integer min = min(list);
        System.out.println(list + " Min: " + min);

        //Returns the maximum element of the given collection, according to the natural ordering of its elements.
        Integer max = max(list);
        System.out.println(list + " Max: " + max);

        //Rotates the elements in the specified list by the specified distance.
        rotate(list, 3);
        System.out.println(list);

        // Replaces all of the elements of the specified list with the specified element.
        fill(list, 5);
        System.out.println(list);

        //Returns an empty list (immutable).  This list is serializable.
        List<Integer> emptyList = Collections.emptyList();

        //Returns an unmodifiable view of the specified list.  This method allows modules to provide users with "read-only" access to internal lists.
        //Query operations on the returned list "read through" to the specified list, and attempts to modify the returned list, whether direct or via its iterator, result in an UnsupportedOperationException.
        List<Integer> unmodifiableList = Collections.unmodifiableList(emptyList);

        //Returns a synchronized (thread-safe) list backed by the specified list.
        List<Integer> synchronizedList = Collections.synchronizedList(emptyList);

        //Returns an empty set (immutable).  This set is serializable.
        Set<Integer> emptySet = Collections.emptySet();

        //Returns an unmodifiable view of the specified set.  This method allows modules to provide users with "read-only" access to internal sets.
        //Query operations on the returned set "read through" to the specified set, and attempts to modify the returned set, whether direct or via its iterator, result in an UnsupportedOperationException.
        Set<Integer> unmodifiableSet = Collections.unmodifiableSet(emptySet);

        //Returns a synchronized (thread-safe) set backed by the specified set. In order to guarantee serial access, it is critical that all access to the backing set is accomplished through the returned set.
        //It is imperative that the user manually synchronize on the returned set when iterating over it:
        Set<Integer> synchronizedSet = Collections.synchronizedSet(emptySet);

        //Returns an empty map (immutable).  This map is serializable.
        Map<Integer, Integer> emptyMap = Collections.emptyMap();

        //Returns an unmodifiable view of the specified map.  This method allows modules to provide users with "read-only" access to internal maps.
        //Query operations on the returned map "read through" to the specified map, and attempts to modify the returned map, whether direct or via its collection views, result in an UnsupportedOperationException
        Map<Integer, Integer> unmodifiableMap = Collections.unmodifiableMap(emptyMap);

        //Returns a synchronized (thread-safe) map backed by the specified map. In order to guarantee serial access, it is critical that all access to the backing map is accomplished through the returned map.
        //It is imperative that the user manually synchronize on the returned map when iterating over any of its collection views:
        Map<Integer, Integer> synchronizedMap = Collections.synchronizedMap(emptyMap);

        System.out.println("---------------------------------------");
    }

    private static void queues() {
/*
        public interface Queue<E> extends Collection<E> {
        public interface Deque<E> extends Queue<E> {
        public abstract class AbstractQueue<E> extends AbstractCollection<E> implements Queue<E> {

        public interface BlockingQueue<E> extends Queue<E> {
        public interface TransferQueue<E> extends BlockingQueue<E> {
        public interface BlockingDeque<E> extends BlockingQueue<E>, Deque<E> {
*/
/*
        An unbounded TransferQueue based on linked nodes.
        This queue orders elements FIFO (first-in-first-out) with respect to any given producer.
        The head is that element that has been on the queue the longest time for some producer, and tail that has been on the queue the shortest time for some producer.

        size() is not a constant-time operation, as due to asynchronous nature of these queues, determining the current number of elements requires a traversal of the elements, and so may report inaccurate results if this collection is modified during traversal.
        Bulk operations addAll, removeAll, retainAll, containsAll, equals, and toArray are not guaranteed to be performed atomically.
        For ex., an iterator operating concurrently with addAll() might view only some of the added elements.

        Memory consistency effects: Actions in a thread prior to placing an object into a LinkedTransferQueue happen-before actions subsequent to the access or removal of that element from the LinkedTransferQueue in another thread.
*/

        //public class LinkedTransferQueue<E> extends AbstractQueue<E> implements TransferQueue<E>, java.io.Serializable {
        TransferQueue<Integer> linkedTransferQueue = new LinkedTransferQueue<>();

        //public class ArrayBlockingQueue<E> extends AbstractQueue<E> implements BlockingQueue<E>, java.io.Serializable {
        BlockingQueue<Integer> arrayBlockingQueue = new ArrayBlockingQueue<>(10);

        //public class LinkedBlockingQueue<E> extends AbstractQueue<E> implements BlockingQueue<E>, java.io.Serializable {
        BlockingQueue<Integer> linkedBlockingQueue = new LinkedBlockingQueue<>();

        //public class PriorityBlockingQueue<E> extends AbstractQueue<E> implements BlockingQueue<E>, java.io.Serializable {
        BlockingQueue<Integer> priorityBlockingQueue = new PriorityBlockingQueue<>();

        //public class SynchronousQueue<E> extends AbstractQueue<E> implements BlockingQueue<E>, java.io.Serializable {
        BlockingQueue<Integer> synchronousQueue = new SynchronousQueue<>();

        //public class LinkedBlockingDeque<E> extends AbstractQueue<E> implements BlockingDeque<E>, java.io.Serializable {
        BlockingDeque<Integer> linkedBlockingDeque = new LinkedBlockingDeque<>();

        //public class ConcurrentLinkedDeque<E> extends AbstractCollection<E> implements Deque<E>, java.io.Serializable {
        Deque<Integer> concurrentLinkedDeque = new ConcurrentLinkedDeque<>();

        //public class ConcurrentLinkedQueue<E> extends AbstractQueue<E> implements Queue<E>, java.io.Serializable {
        Queue<Integer> concurrentLinkedQueue = new ConcurrentLinkedQueue<>();

        //public class PriorityQueue<E> extends AbstractQueue<E> implements java.io.Serializable {
        Queue<Integer> priorityQueue = new PriorityQueue<>();

        //public class ArrayDeque<E> extends AbstractCollection<E> implements Deque<E>, Cloneable, Serializable
        Queue<Integer> arrayDeque = new ArrayDeque<>(); // Deque should be preferred over Stack

        //public class LinkedList<E> extends AbstractSequentialList<E> implements List<E>, Deque<E>, Cloneable, java.io.Serializable
        Queue<Integer> linkedList = new LinkedList<>();

    }

    private static void lists() {
        //public interface List<E> extends Collection<E>

        //If multiple threads access an list(linked or array) instance concurrently, and at least one of the threads modifies the list structurally(adds or deletes one or more elements,
        // or explicitly resizes the backing array), it must be synchronized externally.


        //public class LinkedList<E> extends AbstractSequentialList<E> implements List<E>, Deque<E>, Cloneable, java.io.Serializable
        //Doubly-linked list implementation, Non-synchronized
        //The iterators returned by this class's iterator and listIterator methods are fail-fast: They use the modCount field to check for concurrent modifications
        //If the list is structurally modified at any time after the iterator is created, in any way except through the Iterator's own remove() or add(), the iterator will throw a ConcurrentModificationException.
        //
        //Fail-fast iterators throw ConcurrentModificationException on a best-effort basis.Therefore, it would be wrong to write a program that depended on this exception for its correctness.
        List<Integer> linkedList = new LinkedList<>();


        //public class ArrayList<E> extends AbstractList<E> implements List<E>, RandomAccess, Cloneable, java.io.Serializable
        //Constructs an empty list with an initial capacity of ten, and uses **Object[]** to sktore the elements
        // add() ensureCapacity and grows size by 50% for new array and copies over original array's contents, if necessary
        List<Integer> arrayList = new ArrayList<>();


        //public class Stack<E> extends Vector<E>
        //A last-in-first-out(LIFO) stack of objects.
        //push() and pop() are provided to add/remove items in stack
        //peek() - Looks at top item of this stack without removing it
        //empty() - checks if stack is empty
        //search() - Search the stack for an item and discover how far it is from the top(returns a 1-based answer).
        Stack<Integer> stack = new Stack<>();   // Deque should be preferred over Stack
    }

    private static void sets() {
        //public interface Set<E> extends Collection<E>
        //public abstract class AbstractCollection<E> implements Collection<E>
        //public abstract class AbstractSet<E> extends AbstractCollection<E> implements Set<E>
        //public interface SortedSet<E> extends Set<E>
        //public interface NavigableSet<E> extends SortedSet<E>


        //public class HashSet<E> extends AbstractSet<E> implements Set<E>, Cloneable, java.io.Serializable
        Set<String> hashSet = new HashSet<>();

        //public class TreeSet<E> extends AbstractSet<E> implements NavigableSet<E>, Cloneable, java.io.Serializable
        Set<String> treeSet = new TreeSet<>();

        Set<String> sortedSet = new SortedSet<String>() {
            @Override
            public Comparator<? super String> comparator() {
                return null;
            }

            @Override
            public SortedSet<String> subSet(String fromElement, String toElement) {
                return null;
            }

            @Override
            public SortedSet<String> headSet(String toElement) {
                return null;
            }

            @Override
            public SortedSet<String> tailSet(String fromElement) {
                return null;
            }

            @Override
            public String first() {
                return null;
            }

            @Override
            public String last() {
                return null;
            }

            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean contains(Object o) {
                return false;
            }

            @Override
            public Iterator<String> iterator() {
                return null;
            }

            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @Override
            public <T> T[] toArray(T[] a) {
                return null;
            }

            @Override
            public boolean add(String s) {
                return false;
            }

            @Override
            public boolean remove(Object o) {
                return false;
            }

            @Override
            public boolean containsAll(Collection<?> c) {
                return false;
            }

            @Override
            public boolean addAll(Collection<? extends String> c) {
                return false;
            }

            @Override
            public boolean retainAll(Collection<?> c) {
                return false;
            }

            @Override
            public boolean removeAll(Collection<?> c) {
                return false;
            }

            @Override
            public void clear() {

            }
        };

        //public abstract class EnumSet<E extends Enum<E>> extends AbstractSet<E> implements Cloneable, java.io.Serializable
        EnumSet<Color> enumSet = EnumSet.of(Color.BLUE, Color.GREEN, Color.RED);

        //public class LinkedHashSet<E> extends HashSet<E> implements Set<E>, Cloneable, java.io.Serializable
        Set<String> linkedHashSet = new LinkedHashSet<>();

        //public class CopyOnWriteArraySet<E> extends AbstractSet<E> implements java.io.Serializable
        Set<String> copyOnWriteArraySet = new CopyOnWriteArraySet<>();

        //public class ConcurrentSkipListSet<E> extends AbstractSet<E> implements NavigableSet<E>, Cloneable, java.io.Serializable
        Set<String> concurrentSkipListSet = new ConcurrentSkipListSet<>();

    }

    private static void maps() {
        //public interface Map<K,V> {
        //public abstract class AbstractMap<K,V> implements Map<K,V>
        //public interface SortedMap<K,V> extends Map<K,V> {
        //public interface ConcurrentMap<K, V> extends Map<K, V> {
        //public interface NavigableMap<K,V> extends SortedMap<K,V> {
        //public interface ConcurrentNavigableMap<K,V> extends ConcurrentMap<K,V>, NavigableMap<K,V> {



/*
        Constructs an empty HashMap with the default initial capacity (16) and the default load factor (0.75).
*/
        Map<String, String> hashMap = new HashMap<>();
        Map<String, String> linkedHashMap = new LinkedHashMap<>();
        Map<Color, String> enumMap = new EnumMap<>(Color.class);
        Map<String, String> identityHashMap = new IdentityHashMap<>();
        Map<String, String> treeMap = new TreeMap<>();



        //public class ConcurrentHashMap<K,V> extends AbstractMap<K,V> implements ConcurrentMap<K,V>, Serializable
        Map<String, String> concurrentHashMap = new ConcurrentHashMap<>();

        //public class ConcurrentSkipListMap<K,V> extends AbstractMap<K,V> implements ConcurrentNavigableMap<K,V>, Cloneable, Serializable
        Map<String, String> concurrentSkipListMap = new ConcurrentSkipListMap<>();



    }

    enum Color { RED, GREEN, BLUE};
}
