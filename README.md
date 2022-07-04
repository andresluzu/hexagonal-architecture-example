# Hexagonal Architecture Example

1\. Overview
============

In this guide, we'll understand the main concepts of **Hexagonal Architecture** documented in 2005 by Alistair Cockburn, also called **Port & Adapters Architecture**. We'll walk through a practical Java example to see how it works, and how components relate to each other to provide advantages to the system design.

2\. Concepts
============

The main goal of this architecture is to deal with the **separation of concerns principle**. The business domain is at the center of the application with no dependencies on other components, technology, or infrastructure.

*   **Application:** Contains the **Domain Model** where the business logic resides within all services and orchestration needed to meet the use cases.
*   **Ports:** Are the entry-points -interfaces- that external actors use to communicate with the **Application**.
*   **Adapters:** Are infrastructure-dependent components, that interact with the **Application** through **Ports**.
    *   **Driving:** Primary actors that trigger interactions with the **Application**. For example, a driving adapter could be a console user interface getting user inputs and sending them to the **Application** using a **Port**.
    *   **Driven:** Secondary actors used by the **Application** to achieve a goal, such as persisting data into a database. This type of adapter is **triggered by the Application**.

3\. Immune System Example
=========================

Let's imaging a very basic earth-live immune system, that produces antibodies in response to antigens.

3.1. Application - Domain Model
-------------------------------

Our _Antigen_ class has a _value_ property to identify itself from other antigens.

    public class Antigen {
        private int value;
    
        public Antigen(int value) {
            this.value = value;
        }
    
        // standard getter
    }

An _Antibody_ represents the immune response to an _Antigen._ The _effort_ property measures the work the _Lymphatic__System_ has made to produce the immune response.

    public class Antibody {
        private Antigen antigen;
        private int effort;
    
        public Antibody(Antigen antigen, int effort) {
            this.antigen = antigen;
            this.effort = effort;
        }
        // standard getters and setters
    }

To simulate the effort needed to produce antibodies, our _LymphaticSystem_ uses a loop to randomly find a match with the _Antigen_ value. Each iteration will increment the effort by 1. Before starting the loop, we validate the _Antigen_ to ensure it is not null, and its value is between zero and _MAX\_ANTIGEN\_VALUE_. An _InvalidAntigenException_ is thrown when an Antigen does not meet these requirements.

    public class LymphaticSystem {
        public static int MAX_ANTIGEN_VALUE = 100;
        private static Random RANDOM = new Random();
    
        public static Antibody produce(Antigen antigen) throws InvalidAntigenException {
            // Validate the antigen
            validate(antigen);
    
            // Work for an antibody
            int value;
            int effort = 0;
            do {
                value = RANDOM.nextInt(MAX_ANTIGEN_VALUE);
                effort++;
            } while (antigen.getValue() != value);
    
            return new Antibody(antigen, effort);
        }
    
        private static void validate(Antigen antigen) throws InvalidAntigenException {
            if (isNull(antigen)) {
                throw new InvalidAntigenException(null);
            }
    
            if (antigen.getValue() < 0 || antigen.getValue() >= MAX_ANTIGEN_VALUE) {
                throw new InvalidAntigenException(antigen.getValue());
            }
        }
    }

3.2. Application - Port and Services
------------------------------------

We first need to define a **Port** to let **Driving** **Adapters** initiate interactions with our **Application**. The following _ImmuneService_ interface exposes the method **Adapters** can use to communicate.

    public interface ImmuneService {
        Antibody respond(Antigen antigen) throws InvalidAntigenException;
    }

Let's now write a simple implementation of the _ImmuneService_ to call our **Domain Model**. Notice how it uses an _ImmuneStore_ to look for known antigens before calling the _LymphaticSystem_ to produce the _Antibody_.

> Just like in the real world, our immune system has the capacity to remember an _Antigen_ and produce the correct _Antibody,_ with less effort than the first time it was introduced into the body.

    public class ImmuneServiceImpl implements ImmuneSystem {
        private ImmuneStore store;
    
        public ImmuneServiceImpl(ImmuneStore store) {
            this.store = store;
        }
    
        @Override
        public Antibody respond(Antigen antigen) throws InvalidAntigenException {
            // Search for antibody in immune store or produce it
            Antibody antibody = store.find(antigen).orElse(LymphaticSystem.produce(antigen));
    
            // Save antibody
            store.save(antibody);
    
            return antibody;
        }
    }

_ImmuneMemory_ interface is another **Port** our application services use to communicate with data storage technology.  From the **Application** perspective, there should be no dependencies on infrastructure, it is the **Adatper** responsibility to deal with those. We'll see some _ImmuneMemory_ implementations in the next sections.

    public interface ImmuneStore {
        void save(Antibody antibody);
        Optional<Antibody> find(Antigen antigen);
    }

3.3. Driving Adapter - User Interface
-------------------------------------

In our example, we'll use a basic console user interface, to create instances of the _Antigen_ class and start the interactions with the application through the _ImmuneService._ To keep things simple, we statically instantiate _Scanner, ImmuneStore_, and _ImmuneService_ in _ConsoleUI_. The user interface will accept only integers as valid input and handle _InvalidAntigenException_ when values are out of the application boundaries.

    public class ConsoleUI {
        // static services initialization
        public static void main(String[] args) {
            final String messageFormat = "\nEnter antigen value (%d to %d) or '0' to exit: ";
            int value = 0;
            do {
                System.out.print(String.format(messageFormat, 1, LymphaticSystem.MAX_ANTIGEN_VALUE - 1));
                try {
                    value = scanner.nextInt();
                    Antibody antibody = service.respond(new Antigen(value));
                    System.out.println(antibody);
                } catch (InvalidAntigenException e) {
                    System.out.println("Invalid antigen value: " + e.getValue());
                } catch (Exception e) {
                    System.out.println("Antigen value must be an integer");
                }
            } while (value != 0);
        }
    }

Here are some input samples:

    Enter antigen value (1 to 99) or '0' to exit: 5
    Antibody for antigen=5 with effort=41
    
    Enter antigen value (1 to 99) or '0' to exit: 5
    Antibody for antigen=5 with effort=0
    
    Enter antigen value (1 to 99) or '0' to exit: 8
    Antibody for antigen=8 with effort=113

3.4. Driven Adapter - Data Storage
----------------------------------

Now our _ImmuneStore_ needs implementation for remembering immune responses.  The following _MemoryImmuneStore_ class will act as a **Driven Adapter** for an in-memory database of antigens using _HashMap_.

    public class MemoryImmuneStore implements ImmuneStore {
        private Map<Integer, Antigen> antigenMap = new HashMap<>();
    
        @Override
        public void save(Antibody antibody) {
            antigenMap.putIfAbsent(antibody.getAntigen().getValue(), antibody.getAntigen());
        }
    
        @Override
        public Optional<Antibody> find(Antigen antigen) {
            return antigenMap.entrySet().stream()
              .filter(entry -> entry.getKey().equals(antigen.getValue()))
              .map(Map.Entry::getValue)
              .map(a -> new Antibody(a, 0))
              .findAny();
        }
    }

Notice when an _Antigen_ is found in _antigenMap,_ the implementation creates an _A__ntibody_ with zero effort, mining there was no need to produce it in the _LymphaticSystem._

3.5. Testing
------------

One of the great advantages of this architecture is that our code can be easily tested in isolation. Let's take _ImmuneServiceImpl_ for example.  It depends on the _ImmuneStore_ port, and we need to write a unit test for not remembering any _Antigen_, so we create a helper test class _FakeImmuneStore_.

    public class FakeImmuneStore implements ImmuneStore {
        int saveCounter = 0;
        int findCounter = 0;
    
        @Override
        public void save(Antibody antibody) {
            saveCounter++;
        }
    
        @Override
        public Optional<Antibody> find(Antigen antigen) {
            findCounter++;
            return Optional.empty();
        }
    }

We can see how our _ImmuneServiceImplUnitTest_ uses _FakeImmuneStore_ to ensure store emptiness, and also to run other assertions on the number of methods interactions.

    class ImmuneServiceImplUnitTest {
        FakeImmuneStore store;
        ImmuneServiceImpl service;
    
        @BeforeEach
        void setUp() {
            this.store = new FakeImmuneStore();
            this.service = new ImmuneServiceImpl(store);
        }
    
        @Test
        void givenUnknownAntigens_whenRespond_thenReturnAntibody() throws InvalidAntigenException {
            final int size = 5;
            for (int i = 0; i < size; i++) {
                // Given a valid antigen
                Antigen antigen = new Antigen(i);
    
                // When calling respond
                Antibody antibody = service.respond(antigen);
    
                // Then antibody is returned
                assertNotNull(antibody);
                assertEquals(antigen, antibody.getAntigen());
                assertTrue(antibody.getEffort() > 0);
            }
    
            // Assert calls size
            assertEquals(size, store.findCounter);
            assertEquals(size, store.saveCounter);
        }
    }

4\. Conclusion
==============

Some of the benefits of Hexagonal Architecture are clear while writing the code, but it is worth listing the most relevant:

*   DDD Compatible
*   Easier testing in isolation
*   Independence from external actors
*   Replaceable Adapters
*   Design by purpose not by technology

As a precaution, we can say that it won't be a good idea to use this design in cases where the business logic is small or does not exist at all.
