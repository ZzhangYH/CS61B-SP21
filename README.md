# CS 61B. Data Structures, Spring 2021

Great thanks to [Josh Hug](https://www2.eecs.berkeley.edu/Faculty/Homepages/joshhug.html) and [UC Berkeley](https://www.berkeley.edu) for offering such a great course. This is my self-learning notes when auditing CS 61B @ Summer 2023.

<details>
  <summary><b>Table of Contents</b></summary>

- [Projects Overview](#projects-overview)
- [Week 1](#week-1)
  - [Lecture 1: Hello World Java](#lecture-1-hello-world-java)
  - [Lecture 2: Defining and Using Classes](#lecture-2-defining-and-using-classes)
    - [Compilation](#compilation)
    - [Defining and Instantiating Classes](#defining-and-instantiating-classes)
    - [Static vs. Instance Members](#static-vs-instance-members)
    - [Managing Complexity with Helper Methods](#managing-complexity-with-helper-methods)

</details>

## Projects Overview

List of projects for the course and my stats:

| Projects                                                                              | Start Date | End Date | Autograder          |
| ------------------------------------------------------------------------------------- | ---------- | -------- | ------------------- |
| [Project 0: 2048](https://sp21.datastructur.es/materials/proj/proj0/proj0)            | May 14     | May 14   | **640** / `640 pts` |
| [Project 1: Data Structures](https://sp21.datastructur.es/materials/proj/proj1/proj1) |            |          |                     |
| [Project 2: Gitlet](https://sp21.datastructur.es/materials/proj/proj2/proj2)          |            |          |                     |
| [Project 3: BYOW](https://sp21.datastructur.es/materials/proj/proj3/proj3)            |            |          |                     |

## Week 1

[`Lab1 Setup`](https://sp21.datastructur.es/materials/lab/lab1setup/lab1setup) [`Lab1`](https://sp21.datastructur.es/materials/lab/lab1/lab1) [`HW0`](https://sp21.datastructur.es/materials/hw/hw0/hw0)

### Lecture 1: Hello World Java

**Java is an object-oriented language with strict requirements:**
- Every file should contain a class declaration
- Codes live in classes
- Define main method using `public static void main(String[] args)`

**Java is strictly typed:**
- All variables, parameters, methods need type declaration
- Once declared, never change
- Expressions also have types
- ***Compiler checks type error before execution***

`javac` - **Compile**, `java` - **Run**

### Lecture 2: Defining and Using Classes

#### Compilation

```mermaid
graph LR;
  s1([Hello.java]) -- "Compiler" --> s2
  s2([Hello.class]) -- "Intepreter" --> s3
  s3[[Execution]]
```

**Why** `class` **files?**
- Type checked
- Simpler for machine to execute

#### Defining and Instantiating Classes

- Every method is associated with some classes
- Need a main method to run a class
- But not all classes have a main method

**Defining a class** *(a typical approach)*

`Instance variable`, `Constructor`, `Methods`

**Instantiate an object**
1. ***Declaration*** `Dog dog;`
2. ***Instantiation*** `new Dog();`
3. ***Assignment*** `dog = new Dog();`
4. ***Invocation*** `dog.makeNoise();`

**Create array of objects**
1. Use `new` to create an array `Dog[] dogs = new Dog[2];`
2. Use `new` again instantiate each object in the array `dogs[0] = new Dog();`

#### Static vs. Instance Members

***A class may have a mix of static and non-static members.***

**Why static?** `x = Math.round(5.6)` -> some classes never need instantiation

**Key differences**
- Static methods are invoked using class names `Dog.makeNoise();`
- Instance methods are invoked using instance names `smallDog.makeNoise();`
- Static method cannot access "myself" instance varible because there is no `this`

#### Managing Complexity with Helper Methods

**Why those classes and static methods?**
- Fewer choices for programmers
- Fewer ways to do things
- ***Less Complexity***

**Helper Methods:**
- Decompose large problems into small ones
- Make fewer mistakes by focusing on one single task
- Easier to debug




