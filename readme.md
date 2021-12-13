# In-Memory-Store Locking-Library.

[![jkrsoftware.de Logo](https://jkrprojects-static-files-993857686066.s3.eu-central-1.amazonaws.com/static-images/project-logos/jkrsoftware.de_250x69.png)](https://www.jkrsoftware.de)<br />
project is developed by [jkrsoftware.de](https://www.jkrsoftware.de).

---

[![latest published version](https://badgen.net/maven/v/maven-central/one.jkr.de.jkrsoftware.entity.locking.libraries/InMemoryStore-Locking-Library)](https://s01.oss.sonatype.org/content/repositories/public/one/jkr/de/jkrsoftware/entity/locking/libraries/InMemoryStore-Locking-Library/)
[![last commit on main-branch](https://badgen.net/github/last-commit/jkrsoftware-de/InMemoryStore-Locking-Library/main)](https://github.com/jkrsoftware-de/InMemoryStore-Locking-Library/commit/main)
[![current watchers](https://badgen.net/github/watchers/jkrsoftware-de/InMemoryStore-Locking-Library)](https://github.com/jkrsoftware-de/InMemoryStore-Locking-Library/watchers)
[![given stars](https://badgen.net/github/stars/jkrsoftware-de/InMemoryStore-Locking-Library)](https://github.com/jkrsoftware-de/InMemoryStore-Locking-Library/stargazers)

---

## 💬 Contact Me.

If you need any help or just want to talk to me, have a look here: [contact-informations.md](contact-informations.md).

---

## 🔧 How to use this Library.

---

### ➜ Import the Library via your favourite Dependency-Management-Tool.

All my public Libraries are released to Maven Central, so use it right away from there, if you want.

```
<dependency>
    <groupId>one.jkr.de.jkrsoftware.entity.locking.libraries</groupId>
    <artifactId>InMemoryStore-Locking-Library</artifactId>
    <version>[the Version, you would like to use.]</version>
</dependency>
```

Currently, there is no Plan to change the fundamental API of the *Generic Locking-Library* and *all other Locking-Libraries*, so I don't
provide any Information about previous Releases.<br />
I recommend to use always the latest available Version.

If it will be good to change the existing API, I will provide Release-Informations about past/previous and upcoming Releases over an own
Plattform — as Part of the **jkrsoftware Promise, to keep every Software-Project maintained in my Lifetime**.

---

## 📙 About the „Entity Locking“-Libraries.

I'm developing the Entity Locking-Libraries, cause in many of my current (and future) Software-Products, I need an easy Way to lock
Entities, such as:

* Payment-Transactions for Reality.
* Payment-Transactions for own Gaming-Platforms.
* virtual Bank-Accounts for own Gaming-Platforms.
* User-Accounts

and so on.

The **Entity-Locking-Libraries** providing me (and potentially all other Humans) an **easy-(to-use/to-implement/to-understand) Way** to
solve this Problem. :)

If you want to **switch your Lock-Backend**, you can easily switch to **another Abstraction-Variant of the Locking-Library**.

Every Library (except of the Root-Library itself) is depending on the „Generic-Locking-Library“.

**Why didn't I put everything just in one Library? :)**
<br />
This Expansion/Extension-Library-Concept was my first thought.
<br />
I loved this Idea.

I decided to design/develop the Libraries in this Way, cause:

* the Expandability/Understandability is much higher for the Outside-World
* and I've a good Foundation for further Development-Approaches.<br />
  Don't need to update the Core-Library, if I only want to develop a new Implementation.

---

### 📃 Why should I use one of these Libraries?

It's very easy to implement in your Application and easy to understand.

You can have an **Entity-Locking-Solution** right out of the Box for (currently) **_Plain Java_** and
**_Spring Boot-optimized_** Apps.

The Generic-Locking-Library is the Foundation of every Locking-Library developed by me.

You can use the **Generic-Locking-Library** in Combination with your own Lock-Backend-Implementation — or — you use my Implementations, in
the Form of Extension-Libraries, such as:

* **DynamoDB-Locking-Library**
  * for _Spring Boot_-Apps: *DynamoDB-Locking-Library-SpringBoot-Starter*
* **InMemoryStore-Locking-Library**
  * for _Spring Boot_-Apps: *InMemoryStore-Locking-Library-SpringBoot-Starter*
* (coming soon) the **Redis-Locking-Library**
  * for _Spring Boot_-Apps: *Redis-Locking-Library-SpringBoot-Starter*

---

## 📕 About all of my public-accessible (Open-Source) Software-Products.

Read the following Paragraphs, if you want. 😊

---

### 📃 I use the „Ports & Adapters“- / „Hexagonal“-Software-Design.

For every newly-created Software, I use the **„Ports & Adapters“-Design**.

For better understanding — what the **„Ports & Adapters“-Design** is — have a look
here: [Wikipedia (EN) · Hexagonal Architecture](https://en.wikipedia.org/wiki/Hexagonal_architecture_(software))

---

### 📃 The underlying Software-License.

This Software is free forever.

There is nothing to pay and nothing to fulfill, to use/to edit/to (re-)publish my written Software-Code.

*You can also have a look at: [license.md](license.md) in the same Directory.*

---

### 📃 Lifetime-Maintain-Promise.

**I promise, to keep every Software-Project maintained in my Lifetime.**<br />
Every Software-Project, I release, will always be maintained, unless I'll communicate something else.

Currently, each Software-Project is also used by myself.<br />
So I create/maintain the Software for me and for you. 😊

---

