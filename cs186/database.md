The different Specs used to create the Database. Overall architecture is a B+ tree implementing various SQL join algorithms and System-R query optimization

# Homework 1: SQL queries and scalable algorithms
#### CS186, UC Berkeley, Fall 2017
#### Note: *This homework is to be done individually!*
#### Due: 2PM Thursday, 9/7/2017

### Description

In this homework, we will exercise your newly acquired SQL skills. You will be writing queries against Postgres using public data.

### Getting Started
To follow these instructions **use your CS186 Vagrant VM**. If you do not use the VM, your tests may not execute correctly.

First, open up Virtual Box and power on the CS186 virtual machine. Once the machine is booted up, open a terminal and go to the `course-projects` folder you created in hw0.
```
$ cd course-projects
```
Make sure that you switch to the master branch:
```
$ git checkout master
```
It is good practice to run `git status` to make sure that you haven't inadvertently changed anything in the master branch.
Now, you want to add the reference to the staff repository so you call pull the new homework files:
```
$ git remote add staff https://github.com/berkeley-cs186/course.git
$ git fetch staff master
$ git merge staff/master master
```
The `git merge` will give you a warning and a merge prompt if you have made any conflicting changes to master (not really possible with hw1!).

As with hw0, create a new branch for your work: 
```
git checkout -b hw1
```
Now, you should be ready to start the homework, don't forget to push to this branch when you are done with everything.
### About the schema

In this homework we will be working with the famous [Lahman baseball statistics database](http://www.seanlahman.com/baseball-archive/statistics/). The database contains pitching, hitting, and fielding statistics for Major League Baseball from 1871 through 2016.  It includes data from the two current leagues (American and National), four other "major" leagues (American Association, Union Association, Players League, and Federal League), and the National Association of 1871-1875.

The database is comprised of the following main tables:

	MASTER - Player names, DOB, and biographical info
	Batting - batting statistics
	Pitching - pitching statistics
	Fielding - fielding statistics

It is supplemented by these tables:

	AllStarFull - All-Star appearance
	HallofFame - Hall of Fame voting data
	Managers - managerial statistics
	Teams - yearly stats and standings
	BattingPost - post-season batting statistics
	PitchingPost - post-season pitching statistics
	TeamFranchises - franchise information
	FieldingOF - outfield position data
	FieldingPost- post-season fielding data
	ManagersHalf - split season data for managers
	TeamsHalf - split season data for teams
	Salaries - player salary data
	SeriesPost - post-season series information
	AwardsManagers - awards won by managers
	AwardsPlayers - awards won by players
	AwardsShareManagers - award voting for manager awards
	AwardsSharePlayers - award voting for player awards
	Appearances - details on the positions a player appeared at
	Schools - list of colleges that players attended
	CollegePlaying - list of players and the colleges they attended

For more detailed information, see the [docs online](https://github.com/chadwickbureau/baseballdatabank/blob/master/core/readme2014.txt).

### Using Postgres

You can create a database, and start up the command-line interface `psql` to send SQL commands to that database:

	$ createdb test
	$ psql test

The `psql` interface to postgres has a number of built-in commands, all of which begin with a backslash. Use the `\d` command to see a description of your current relations. Use SQL's `CREATE TABLE` to create new relations. You can also enter `INSERT`, `UPDATE`, `DELETE`, and `SELECT` commands at the `psql` prompt. Remember that each command must be terminated with a semicolon (`;`).

Type `help` at the psql prompt to get more help options on `psql` commands or SQL statements.

When you're done, use `\q` or `ctrl-d` to exit `psql`.

If you messed up creating your database, you can issue the `dropdb` command to delete it.

    $ createdb tst  # oops!
    $ dropdb tst   # drops the db named 'tst'

### Getting started

Follow the steps above to test that Postgres is set up properly.

At this point you can load up the sample data:

	$ ./setup.sh

This will take a little while as it extracts and imports into your database. When you are done you have a database called `baseball`.  You can connect to it with `psql` and verify that the schema was loaded with the `\d` command:

	$ psql baseball
	baseball=# \d

Try running a few sample commands in the `psql` console and see what they do:

    baseball=# \d master
    baseball=# SELECT playerid, namefirst, namelast FROM master;
    baseball=# SELECT COUNT(*) FROM fielding;

For queries with many results, you can use arrow keys to scroll through the
results, or the spacebar to page through the results (much like the UNIX [`less`](https://www.tutorialspoint.com/unix_commands/less.htm) command). Press `q` to stop viewing the results.

### Write these queries

We've provided a skeleton solution, `hw1.sql`, to help you get started. In the file, you'll find a `CREATE VIEW` statement for each of the first 4 questions below, specifying a particular view name (like `q2`) and list of column names (like `playerid`, `lastname`). The view name and column names constitute the interface against which we will grade this assignment. In other words, *don't change or remove these names*. Your job is to fill out the view definitions in a way that populates the views with the right tuples.

For example, consider Question 0: "What is the highest `era` ([earned run average](https://en.wikipedia.org/wiki/Earned_run_average)) recorded in baseball history?".

In the `hw1.sql` file we provide:

	CREATE VIEW q0(era) AS
        SELECT 1 -- replace this line
	;

You would edit this with your answer, keeping the schema the same:

	-- solution you provide
	CREATE VIEW q0(era) AS
	 SELECT MAX(era)
	 FROM pitching
	;


To complete the homework, create a view for `q0` as above (via [copy-paste](http://i0.kym-cdn.com/photos/images/original/000/005/713/copypasta.jpg)), and for all of the following queries, which you will need to write yourself.

1. Basics
    1. In the `master` table, find the `namefirst`, `namelast` and `birthyear` for all players with weight greater than 300 pounds.
    2. Find the `namefirst`, `namelast` and `birthyear` of all players whose `namefirst` field contains a space.
    3. From the `master` table, group together players with the same `birthyear`, and report the `birthyear`, average `height`, and number of players for each `birthyear`. Order the results by `birthyear` in *ascending* order.

       Note: some birthyears have no players; your answer can simply skip those years. In some other years, you may find that all the players have a `NULL` height value in the dataset (i.e. `height IS NULL`); your query should return `NULL` for the height in those years.

    4. Following the results of Part iii, now only include groups with an average height > `70`. Again order the results by `birthyear` in *ascending* order.

2. Hall of Fame Schools
    1. Find the `namefirst`, `namelast`, `playerid` and `yearid` of all people who were successfully inducted into the Hall of Fame in *descending* order of `yearid`.

        Note: a player with id `drewj.01` is listed as having failed to be
        inducted into the Hall of Fame, but does not show up in the `master`
        table. Your query may assume that all people inducted into the Hall of Fame
        appear in the `master` table.

    2. Find the people who were successfully inducted into the Hall of Fame and played in college at a school located in the state of California. For each person, return their `namefirst`, `namelast`, `playerid`, `schoolid`, and `yearid` in *descending* order of `yearid`. Break ties on `yearid` by `schoolid, playerid` (ascending). (For this question, `yearid` refers to the year of induction into the Hall of Fame).

        Note: a player may appear in the results multiple times (once per year
        in a college in California).
 
    3. Find the `playerid`, `namefirst`, `namelast` and `schoolid` of all people who were successfully inducted into the Hall of Fame -- whether or not they played in college. Return people in *descending* order of `playerid`. Break ties on `playerid` by `schoolid` (ascending). (Note: `schoolid` will be `NULL` if they did not play in college.)

3. [SaberMetrics](https://en.wikipedia.org/wiki/Sabermetrics)
    1. Find the `playerid`, `namefirst`, `namelast`, `yearid` and single-year `slg` (Slugging Percentage) of the players with the 10 best annual Slugging Percentage recorded over all time. For statistical significance, only include players with more than 50 at-bats in the season. Order the results by `slg` descending, and break ties by `yearid, playerid` (ascending).

       *Baseball note*: Slugging Percentage is not provided in the database; it is computed according to a [simple formula](https://en.wikipedia.org/wiki/Slugging_percentage) you can calculate from the data in the database.

       *SQL note*: You should compute `slg` properly as a floating point number---you'll need to figure out how to convince SQL to do this!

    2. Following the results from Part i, find the `playerid`, `firstname`, `lastname` and `lslg` (Lifetime Slugging Percentage) for the players with the top 10 Lifetime Slugging Percentage. Note that the database only gives batting information broken down by year; you will need to convert to total information (from the earliest date recorded up to the last date recorded) to compute `lslg`. For statistical significance, only include players with more than 50 at-bats throughout their career.

       Order the results by `lslg` descending, and break ties by `playerid` (ascending order).

    3. Find the `namefirst`, `namelast` and Lifetime Slugging Percentage (`lslg`) of batters whose lifetime slugging percentage is higher than that of San Francisco favorite Willie Mays.  For statistical significance, only include players with more than 50 at-bats throughout their career. You may include Willie Mays' playerid in your query (`mayswi01`), but you *may not* include his slugging percentage -- you should calculate that as part of the query. (Test your query by replacing `mayswi01` with the playerid of another player -- it should work for that player as well! We may do the same in the autograder.)

    *Just for fun*: For those of you who are baseball buffs, variants of the above queries can be used to find other more detailed SaberMetrics, like [Runs Created](https://en.wikipedia.org/wiki/Runs_created) or [Value Over Replacement Player](https://en.wikipedia.org/wiki/Value_over_replacement_player). Wikipedia has a nice page on [baseball statistics](https://en.wikipedia.org/wiki/Baseball_statistics); most of these can be computed fairly directly in SQL.

4. Salaries
    1. Find the `yearid`, min, max, average and standard deviation of all player salaries for each year recorded, ordered by `yearid` in *ascending* order.

    2. For salaries in 2016, compute a [histogram](https://en.wikipedia.org/wiki/Histogram). Divide the salary range into 10 equal bins from min to max, with `binid`s 0 through 9, and count the salaries in each bin. Return the `binid`, `low` and `high` values for each bin, as well as the number of salaries in each bin, with results sorted from smallest bin to largest.

       *Note*: `binid` 0 corresponds to the lowest salaries, and `binid` 9 corresponds to the highest. The ranges are left-inclusive (i.e. `[low, high)`) -- so the `high` value is excluded. For example, if bin 2 has a `high` value of 100000, salaries of 100000 belong in bin 3, and bin 3 should have a `low` value of 100000.

       *Note*: The `high` value of bin 9 just needs to be no smaller than the largest salary (the `high` value for only this bin may be inclusive).
   
    3. Now let's compute the Year-over-Year change in min, max and average player salary. For each year with recorded salaries after the first, return the `yearid`, `mindiff`, `maxdiff`, and `avgdiff` with respect to the previous year. Order the output by `yearid` in *ascending* order. (You should omit the very first year of recorded salaries from the result.)

    4. In 2001, the max salary went up by over $6 million. Write a query to find the players that had the max salary in 2000 and 2001. Return the `playerid`, `namefirst`, `namelast`, `salary` and `yearid` for those two years. If multiple players tied for the max salary in a year, return all of them.

        *Note on notation:* you are computing a relational variant of the [argmax](https://en.wikipedia.org/wiki/Arg_max) for each of those two years.


5. Shortest Paths.

    In this question, we're going to study the stars of the Oakland A's, whose weird glory years in the early 1970s formed the subject of the recent book [Dynastic, Bombastic, Fantastic](https://www.amazon.com/Dynastic-Bombastic-Fantastic-Catfish-Charlie/dp/0544303172). Your challenge will be to write a [single-source shortest path](https://en.wikipedia.org/wiki/Shortest_path_problem#Single-source_shortest_paths) algorithm that makes use of SQL for its heavy lifting. We'll take this in pieces. The basic idea is a batch-oriented variant of [Dijkstra's algorithm](https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm), which forms "shortest" paths of increasing hop-count.  The pseudocode looks like this:

    ```
        // (paths): the set of shortest paths we've seen so far
        // (paths_to_update): the set of paths we added at step i
        // (new_paths): the set of paths that are on the frontier of our
        //      exploration, and are of length i+1 hops
        // (better_paths): best paths found up to this iteration

        // Initialize.
        insert all edges of 1 hop into (paths) and (paths_to_update)

        // Iterate.
        // the variable i is unnecessary, but illustrates the fact that we're
        // building paths of i+1 hops out of paths of i hops!
        i = 1
        while there are paths of i hops (paths_to_update):
            // find all paths of i+1 hops
            (extended_paths) = join of paths of i hops (paths_to_update)
                               with 1-hop paths (i.e. edges)
                               to form paths of i+1 hops

            // find paths of i+1 hops that are "better" than our current paths
            (new_paths) = paths of i+1 hops that either
                a. connect two vertices that were previously disconnected
                b. connect two vertices with a shorter-length path than previously found

            (better_paths) = the best paths among those in either (new_paths) or (paths)

            // when we go to the next iteration, need to swap the sets
            (paths) = (better_paths)
            (paths_to_update) = (new_paths)
            (new_paths) = empty set
        i++
    ```

    We begin with the initial graph construction in the `hw1-q5-setup.sql`(./hw1-q5-setup.sql) file; these queries are provided for you.

      - First, we identify the initial "seed" vertices of a graph. These vertices are `playerid` values of A's players who were successful inducted into the hall of fame. (`q5_seeds`)
      - Next we define the edges of the graph: players (on any team!) who played with one of the seed players. Two players are said to play together if they appear in the `appearances` table with matching `teamid` and `yearid` values. **Edit (should not affect your solution code)**: We want bidirectional links between players, so we ensure that every `(src, dest)` pair has a matching `(dest, src)` pair. We are careful to ensure that each pair only appears once, and we don't create "self-cycles" by having the `src` and `dest` of the edge be the same. (`q5_edges`)
      - Next we initialize a table of paths between vertices by starting with paths of length 1: namely, the pairs of `q5_edges`. In the path relation we  store a source node `src`, a destination node `dest`, path-length `length` of 1 (for direct neighbors), and a `path` from the source to the destination, which in this case is simple `[src, dest]`. We use the [Postgres array type](https://www.postgresql.org/docs/9.6/static/arrays.html) for the `path` column. (`q5_paths`)
      - Finally we create the initial set of paths we need to update, which contains all the `q5_paths` to start. (`q5_paths_to_update`)

    Now you will write the workhorse queries that will run in the interior of a simple `while` loop, which we'll keep in the `hw1-q5-while.sql` file. This loop could be implemented in a scripting language like Python, but we'll just run it by hand ourselves. Each iteration of this `while` loop is going to examine paths that are one hop longer than the previous iteration.

      1. Create the `q5_extended_paths` query, which forms paths one hop longer than we've seen before. It should have the same columns as the `q5_paths_to_update` table. Be sure that the output includes a correct length, as well as a correct array of vertices in the `path` attribute. You may want to reference the `PostgreSQL manual on array functions`(https://www.postgresql.org/docs/9.6/static/functions-array.html) to see how to manipulate PostgreSQL arrays. Also, note that although lengths of edges in our setup are all `1`, your code should handle the general case of "weighted" edges with different lengths.
      2. Create the `q5_new_paths` table that should contain paths that either (a) connect previously unconnected nodes, or (b) provide a shorter path between a pair of nodes that were previously connected by a longer path. Again, it should have the same columns as the table from Part i, `q5_extended_paths`.
      3. Now, the `q5_better_paths` table should include the shortest paths found so far, whether they come from the `q5_paths` table or the `q5_new_paths` table. For any two nodes, there should be 0, 1 or more paths; if there are more than 1, they should all have the same length. You may find SQL's [conditional expressions](https://www.postgresql.org/docs/9.6/static/functions-conditional.html) helpful here (e.g. `CASE`, `COALESCE`, `LEAST`, etc.)

    The remaining steps are provided for you:

      - Now we can swap `q5_better_paths` for `q5_paths`, and `q5_new_paths` for `q5_paths_to_update` by renaming them.
      - As a last step, we check to see if `q5_new_paths` is empty, and produce output to inform a user whether to run the loop again or not.

    You can test your code by importing the SQL files from `psql`:

    ```
    % psql baseball
    baseball=# \i /<path-to-your-homework>/hw1-q5-setup.sql'
    baseball=# \i /<path-to-your-homework>/hw1-q5-while.sql'
    baseball=# \i /<path-to-your-homework>/hw1-q5-while.sql'
    baseball=# \i /<path-to-your-homework>/hw1-q5-while.sql'
    ```
    
    ...etc. Until you see a message like this:
    ```
    baseball=# \i hw1-q5-while.sql
    DROP TABLE
    SELECT 0
    SELECT 0
    SELECT 12192
    DROP TABLE
    ALTER TABLE
    DROP TABLE
    ALTER TABLE
     path_count |  status  
    ------------+----------
          12192 | FINISHED
    (1 row)
    ```

    *Note for the curious:* Many social network and web ranking algorithms are based on analyzing graphs in this manner---given the scale of those datasets, an approach using a scalable data processingbackend as we do here is important. Some of these techniques require finding shortest paths as a subroutine. You could extend your code above to compute [betweenness centrality](https://en.wikipedia.org/wiki/Betweenness_centrality), for example, and identify players who are at the center of the hall-of-fame network.

## Testing

You can run questions 1-4 directly using:

	$ psql baseball < hw1.sql

This can help you catch any syntax errors in your SQL.

To help debug your logic, we've provided output from each of the views you need to define in questions 1-4 for the data set you've been given.  Your views should match ours, but note that your SQL queries should work on ANY data set. We reserve the right to test your queries on a (set of) different database(s), so it is *NOT* sufficient to simply return these results in all cases!

To run the test, from within the `hw1` directory:

	$ ./test.sh

Become familiar with the UNIX [diff](http://en.wikipedia.org/wiki/Diff) command, if you're not already, because our tests saves the `diff` for any query executions that don't match in `diffs/`.  If you care to look at the query outputs directly, ours are located in the `expected_output` directory. Your view output should be located in your solution's `your_output` directory once you run the tests.

**Note:** For queries where we don't specify the order, it doesn't matter how
you sort your results; we will reorder before comparing. Note, however, that our
test query output is sorted for these cases, so if you're trying to compare
yours and ours manually line-by-line, make sure you use the proper ORDER BY
clause (you can determine this by looking in `test.sh`).

To help you with question 5, we have provided an alternative setup file, `hw1-q5-test-setup.sql`, which provides a more familiar graph: the states of the United States and their geographic neighbors. You can use it in place of `hw1-q5-setup.sql` in the instructions above. We are not providing test results for question 5, but you can use this dataset to manually verify your results. When it completes, try a query like
```sql
SELECT * FROM q5_paths WHERE src = 'CA' AND dest = 'FL';
```

## Submission
When you are done, run the following git commands similar to like what you did in HW0 to push it to Github. And you are done!
```
$ git add .
$ git commit -m "Your commit message"
$ git push origin hw1
```
# Homework 2: B+ Trees
**Due 2:00 PM Tuesday, September 26**

## Overview
In this assignment, you will implement persistent B+ trees in Java. In this
document, we explain

- how to fetch the release code from GitHub,
- how to program in Java on the virtual machine, and
- what code you have to implement.

## Step 0: Fetching the Assignment
First, **boot your VM and open a terminal window**. Then run the following to
checkout the master branch.

```bash
git checkout master
```

If this command fails, you may be on another branch with uncommited changes.
Run `git branch` to see what branch you are on and `git status` to check for
uncommited changes. Once all changes are committed, run `git checkout master`.

Next, run the following to pull the homework from GitHub and change to a new
`hw2` branch:

```bash
git pull staff master
git checkout -b hw2
```

## Step 1: Getting Started with Java
Navigate to the `hw2` directory. In the
`src/main/java/edu/berkeley/cs186/database` directory, you will find all of the
Java 8 code we have provided to you. In the
`src/test/java/edu/berkeley/cs186/database` directory, you will find all of the
unit tests we have provided to you. To build and test the code with maven, run
the following in the `hw2` directory:

```bash
mvn clean compile # Compile the code.
mvn clean test    # Test the code. Not all tests will pass until you finish the
                  # assignment.
```

You are free to use any text editor or IDE to complete the project, but **we
will build and test your code on the VM with maven**. We recommend completing
the project with either Eclipse or IntelliJ, both of which come installed on
the VM:

```bash
eclipse # Launch eclipse.
idea.sh # Launch IntelliJ.
```

There are instructions online for how to import a maven project into
[Eclipse][eclipse_maven] and [IntelliJ][intellij_maven]. There are also
instructions online for how to debug Java in [Eclipse][eclipse_debugging] and
[IntelliJ][intellij_debugging]. When IntelliJ prompts you for an SDK, select
the one in `/home/vagrant/jdk1.8.0_131`. It bears repeating that even though
you are free to complete the project in Eclipse or IntelliJ, **we will build
and test your code on the VM with maven**.

## Step 2: Getting Familiar with the Release Code
Navigate to the `hw2/src/main/java/edu/berkeley/cs186/database` directory. You
will find five directories: `common`, `databox`, `io`, `table`, and `index`.
You do not have to deeply understand all of the code, but since all future
programming assignments will reuse this code, it's worth becoming a little
familiar with it. **In this assignment, though, you may only modify files in
the `index` directory**.

### common
The `common` directory contains miscellaneous and generally useful bits of code
that are not particular to this assignment.

### databox
Like most DBMSs, the system we are working on in this assignment has its own
type system, which is distinct from the type system of the programming language
used to implement the DBMS. (Our DBMS doesn't quite provide SQL types either,
though it's modeled on a simplified version of SQL types). In this homework,
we'll need to write Java code to create and manipulate the DBMS types and any
data we store.

The `databox` directory contains the classes which represent the values
stored in a database, as well as their types. Specifically, the `DataBox` class
represents values and the `Type` class represents types. Here's an example:

```java
DataBox x = new IntDataBox(42); // The integer value '42'.
Type t = Type.intType();        // The type 'int'.
Type xsType = x.type();         // Get x's type: Type.intType()
int y = x.getInt();             // Get x's value: 42
String s = x.getString();       // An exception is thrown.
```

### io
The `io` directory contains code that allows you to allocate, read, and write
pages to and from a file. All modifications to the pages of the file are
persisted to the file. The two main classes of this directory are
`PageAllocator` which can be used to allocate pages in a file, and `Page` which
represents pages in the file. Here's an example of how to persist data into a
file using a `PageAllocator`:

```java
// Create a page allocator which stores data in the file "foo.data". Setting
// wipe to true clears out any data that may have previously been in the file.
bool wipe = true;
PageAllocator allocator = new PageAllocator("foo.data", wipe);

// Allocate a page in the file. All pages are assigned a unique page number
// which can be used to fetch the page.
int pageNum = allocator.allocPage(); // The page number of the allocated page.
Page page = allocator.fetchPage(pageNum); // The page we just allocated.
System.out.println(pageNum); // 0. Page numbers are assigned 0, 1, 2, ...

// Write data into the page. All data written to the page is persisted in the
// file automatically.
ByteBuffer buf = page.getByteBuffer();
buf.putInt(42);
buf.putInt(9001);
```

And here's an example of how to read data that's been persisted to a file:

```java
// Create a page allocator which stores data in the file "foo.data". Setting
// wipe to false means that this page allocator can read any data that was
// previously stored in "foo.data".
bool wipe = false;
PageAllocator allocator = new PageAllocator("foo.data", wipe);

// Fetch the page we previously allocated.
Page page = allocator.fetchPage(0);

// Read the data we previously wrote.
ByteBuffer buf = page.getByteBuffer();
int x = buf.getInt(); // 42
int y = buf.getInt(); // 9001
```

### table
In future assignments, the `table` directory will contain an implementation of
relational tables that store values of type `DataBox`. For now, it only
contains a `RecordId` class which uniquely identifies a record on a page by its
page number and entry number.

```java
// The jth record on the ith page.
RecordId rid = new RecordId(i, (short) j);
```

### index
We describe the `index` directory in the next section.

## Step 3: Implementing B+ Trees
The `index` directory contains an partial implementation of a B+ tree
(`BPlusTree`), an implementation that you will complete in this assignment.
Every B+ tree maps keys of type `DataBox` to values of type `RecordId`. A B+
tree is composed of inner nodes (`InnerNode`) and leaf nodes (`LeafNode`).
Every B+ tree is persisted to a file, and every inner node and leaf node is
stored on its own page.

In this assignment, do the following:

1. Read through all of the code in the `index` directory. Many comments contain
   critical information on how you must implement certain functions. For
   example, `BPlusNode::put` specifies how to redistribute entries after a
   split. You are responsible for reading these comments. If you do not obey
   the comments, you will lose points. Here are a few of the most notable
   points:
    - Our implementation of B+ trees does not support duplicate keys. You will
      throw an exception whenever a duplicate key is inserted.
    - Our implementation of B+ trees assumes that inner nodes and leaf nodes
      can be serialized on a single page. You do not have to support nodes that
      span multiple pages.
    - Our implementation of delete does not rebalance the tree. Thus, the
      invariant that all non-root leaf nodes in a B+ tree of order `d` contain
      between `d` and `2d` entries is broken.
2. Implement the `LeafNode::fromBytes` function that reads a `LeafNode` from a
   page. For information on how a leaf node is serialized, see
   `LeafNode::toBytes`. For an example on how to read a node from disk, see
   `InnerNode::fromBytes`.
3. Implement the `get`, `getLeftmostLeaf`, `put`, and `remove` methods of
   `InnerNode` and `LeafNode`. For information on what these methods do, refer
   to the comments in `BPlusNode`. Don't forget to call `sync` when
   implementing `put` and `remove`; it's easy to forget.
4. Implement the second constructor of `BPlusTree` which reads a `BPlusTree`
   from disk. See the first `BPlusTree` constructor and
   `BPlusTree::writeHeader` for information on how a `BPlusTree` is serialized.
5. Implement the `get`, `scanAll`, `scanGreaterEqual`, `put`, and `remove`
   methods of `BPlusTree`. In order to implement `scanAll` and
   `scanGreaterEqual`, you will have to complete the `BPlusTreeIterator` class.

After this, you should pass all the tests we have provided to you (and any you
add yourselves).

Note that you may not modify the signature of any methods or classes that we
provide to you (except `BPlusTreeIterator`), but you're free to add helper
methods. Also, you may only modify code in the `index` directory.

## Step 4: Submitting the Assignment
After you complete the assignment, simply commit and `git push` your `hw2`
branch. 60% of your grade will come from passing the unit tests we provide to
you. 40% of your grade will come from passing unit tests that we have not
provided to you. If your code does not compile on the VM with maven, we reserve
the right to give you a 0 on the assignment.

[eclipse_maven]: https://stackoverflow.com/a/36242422
[intellij_maven]: https://www.jetbrains.com/help/idea//2017.1/importing-project-from-maven-model.html
[eclipse_debugging]: http://www.vogella.com/tutorials/EclipseDebugging/article.html
[intellij_debugging]: https://www.jetbrains.com/help/idea/debugging.html

# Homework 3: Iterators and Join Algorithms
**Due 2:00 PM Tuesday, October 17**

## Overview
In this assignment, you will implement iterators  and join algorithms over tables in Java. In this
document, we explain

- how to fetch the release code from GitHub,
- how to program in Java on the virtual machine, and
- what code you have to implement.

## Step 0: Fetching the Assignment
First, **boot your VM and open a terminal window**. Then run the following to
checkout the master branch.

```bash
git checkout master
```

If this command fails, you may be on another branch with uncommited changes.
Run `git branch` to see what branch you are on and `git status` to check for
uncommited changes. Once all changes are committed, run `git checkout master`.

Next, run the following to pull the homework from GitHub and change to a new
`hw3` branch:

```bash
git pull staff master
git checkout -b hw3
```

## Step 1: Getting Started with Java
Navigate to the `hw3` directory. In the
`src/main/java/edu/berkeley/cs186/database` directory, you will find all of the
Java 8 code we have provided to you. In the
`src/test/java/edu/berkeley/cs186/database` directory, you will find all of the
unit tests we have provided to you. To build and test the code with maven, run
the following in the `hw3` directory:

```bash
mvn clean compile # Compile the code.
mvn clean test    # Test the code. Not all tests will pass until you finish the assignment.
```

You are free to use any text editor or IDE to complete the project, but **we
will build and test your code on the VM with maven**. We recommend completing
the project with either Eclipse or IntelliJ, both of which come installed on
the VM:

```bash
eclipse # Launch eclipse.
idea.sh # Launch IntelliJ.
```

There are instructions online for how to import a maven project into
[Eclipse][eclipse_maven] and [IntelliJ][intellij_maven]. There are also
instructions online for how to debug Java in [Eclipse][eclipse_debugging] and
[IntelliJ][intellij_debugging]. When IntelliJ prompts you for an SDK, select
the one in `/home/vagrant/jdk1.8.0_131`. It bears repeating that even though
you are free to complete the project in Eclipse or IntelliJ, **we will build
and test your code on the VM with maven**.

## Step 2: Getting Familiar with the Release Code
Navigate to the `hw3/src/main/java/edu/berkeley/cs186/database` directory. You
will find six directories: `common`, `databox`, `io`, `table`, `index`, and `query`, and two files, `Database` and `DatabaseException`.
You do not have to deeply understand all of the code, but since all future
programming assignments will reuse this code, it's worth becoming a little
familiar with it. **In this assignment, though, you may only modify files in
the `query` and `table` directories**. See the Homework 2 specification for information on `databox`, `io`, and `index`.

### common
The `common` directory now contains an interface called a `BacktrackingIterator`. Iterators that implement this will be able to mark a point during iteration, and reset back to that mark. For example, here we have a back tracking iterator that just returns 1, 2, and 3, but can backtrack:

```java
BackTrackingIterator<Integer> iter = new BackTrackingIteratorImplementation();
iter.next(); //returns 1
iter.next(); //returns 2
iter.mark();
iter.next(); //returns 3
iter.hasNext(); //returns false
iter.reset();
iter.hasNext(); // returns true
iter.next(); //returns 2

```
`ArrayBacktrackingIterator` implements this interface. It takes in an array and returns a backtracking iterator over the values in that array.

### Table
The `table` directory now contains an implementation of
relational tables that store values of type `DataBox`. The `RecordId` class uniquely identifies a record on a page by its page number and entry number on that page. A `Record` is represented as a list of DataBoxes. A `Schema` is represented as list of column names and a list of column types. A `RecordIterator` takes in an iterator over `RecordId`s for a given table and returns an iterator over the corresponding records. A `Table` is made up of pages, with the first page always being the header page for the file. See the comments in `Table` for how the data of a table is serialized to a file.

### Database
The `Database` class represents a database. It is the interface through which we can create and update tables, and run queries on tables. When a user is operating on the database, they start a `transaction`, which allows for atomic access to tables in the database. You should be familiar with the code in here as it will be helpful when writing your own tests.

### Query
The `query` directory contains what are called query operators. These are operators that are applied to one or more tables, or other operators. They carry out their operation on their input operator(s) and return iterators over records that are the result of applying that specific operator. We call them “operators” here to distinguish them from the Java iterators you will be implementing.

`SortOperator` does the external merge sort algorithm covered in lecture. It contains a subclass called a `Run`. A `Run` is just an object that we can add records to, and read records from. Its underlying structure is a Table.

`JoinOperator` is the base class that join operators you will implement extend. It contains any methods you might need to deal with tables through the current running transaction. This means you should not deal directly with `Table` objects in the `Query` directory, but only through methods given through the current transaction.



## Step 3: Implementing Iterators and Join Algorithms


#### Notes Before You Begin
 In lecture, we sometimes use the words `block` and `page` interchangeably to describe a single unit of transfer from disc. The notion of a `block` when discussing join algorithms is different however. A `page` is a single unit of transfer from disc, and a  `block` is one or more `pages`. All uses of `block` in this project refer to this alternate definition.

 Besides when the comments tell you that you can do something in memory, everything else should be **streamed**. You should not hold more pages in memory at once than the given algorithm says you are allowed to.

  Remember the test cases we give you are not comprehensive, so you should write your own tests to further test your code and catch edge cases. Also, we give you all the tests for the current state of the database, but we skip some of them for time.

  The tests we provide to you for this HW are under `TestTable` for part 1, `TestJoinOperator` for parts 2 and 4, and `TestSortOperator` for part 3. If you are running tests from the terminal (and not an IDE), you can pass `-Dtest=TestName` to `mvn test` to only run a single file of tests.

#### 1. Table Iterators

In the `table` directory, fill in the classes `Table#RIDPageIterator` and `Table#RIDBlockIterator`. The tests in `TestTable` should pass once this is complete.

*Note on testing*: If you wish to write your own tests on `Table#RIDBlockIterator`, be careful with using the `Iterator<Page> block, int maxPages` constructor: you have to get a new `Iterator<Page>` if you want to recreate the iterator in the same test.

#### 2. Nested Loops Joins

Move to the `query` directory. You may first want to take a look at `SNLJOperator`. Complete `PNLJOperator` and `BNLJOperator`. The PNLJ and BNLJ tests in `TestJoinOperator` should pass once this is complete.

#### 3: External Sort

Complete implementing `SortOperator.java`. The tests in `TestSortOperator` should pass once this is complete.

In the hidden tests, we may test the methods independently by replacing other methods with the staff solution, so make sure they each function exactly as described in the comments. This also allows for partial credit should one of your methods not work correctly.

#### 4: Sort Merge Join

Complete implementing `SortMergeOperator.java`. The sort phase of this join should use your previously implemented `SortOperator#sort` method. Note that we do not do the optimization discussed in lecture where the join happens during the last pass of sorting the two tables. We keep the sort phase completely separate from the join phase. The SortMerge tests in `TestJoinOperator` should pass once this is complete.

In the hidden tests, we may test `SortMergeOperator` independently of `SortOperator` by replacing your sort with the staff solution, so make sure it functions as described.

## Step 4: Submitting the Assignment
After you complete the assignment, simply commit and `git push` your `hw3`
branch. 60% of your grade will come from passing the unit tests we provide to
you. 40% of your grade will come from passing unit tests that we have not
provided to you. If your code does not compile on the VM with maven, we reserve
the right to give you a 0 on the assignment.

[eclipse_maven]: https://stackoverflow.com/a/36242422
[intellij_maven]: https://www.jetbrains.com/help/idea//2017.1/importing-project-from-maven-model.html
[eclipse_debugging]: http://www.vogella.com/tutorials/EclipseDebugging/article.html
[intellij_debugging]: https://www.jetbrains.com/help/idea/debugging.html


# Project 4: Cost Estimation and Query Optimization

## Logistics

**Due date: Thursday 11/02/2017, 1:59:59 PM**

## Background
A query optimizer attempts to find the optimal execution plan for a SQL statement. The optimizer selects the plan with the lowest estimated cost among all considered candidate plans. The optimizer uses available statistics to estimate cost. Because the database has many internal statistics and tools at its disposal, the optimizer is usually in a better position than the user to determine the optimal method of statement execution.

For a specific query in a given environment, the cost computation accounts for metrics of query execution such as I/O. For example, consider a query that selects all employees who are managers. If the statistics indicate that 80% of employees are managers, then the optimizer may decide that a full table scan is most efficient. However, if statistics indicate that very few employees are managers and there is an index on that key, then reading an index followed by a table access by rowid may be more efficient than a full table scan.

In this project, you will design two pieces of a relational query optimizer: (1) the cost-estimator and (2) optimal query plan chooser. This project builds on top of the code and functionality that you should be familiar with from previous projects. We have released the staff solutions for HW 3, and you should integrate those into your codebase (see below). 

## Getting Started
First, open up Virtual Box and power on the CS186 virtual machine. Once the machine is booted up, open a terminal and go to the `course-projects` folder you created in hw0.
```
$ cd course-projects
```
Make sure that you switch to the master branch:
```
$ git checkout master
```
It is good practice to run `git status` to make sure that you haven't inadvertently changed anything in the master branch.
Now, you want to add the reference to the staff repository so you call pull the new homework files:
```
$ git fetch staff master
$ git merge staff/master master
```
The `git merge` will give you a warning and a merge prompt if you have made any conflicting changes to master (not really possible with hw1!).

As with hw1, hw2, and hw3, make sure youcreate a new branch for your work: 
```
git checkout -b hw4
```
Now, you should be ready to start the homework. *Don't forget to push to this branch when you are done with everything!*

## Background: Query Interface 
Databases are represented by `Database` objects that can be created as follows:
```java
Database db = new Database('myDBFolder'); 
```
This creates a database where all of the tables will be stored in the `myDBFolder` directory on your filesystem.
The next relevant class is the `Schema` class, which defines table schemas.
`Schema` objects can be created by providing a list of field names and field types:
```
List<String> names = Arrays.asList("boolAttr", "intAttr", 
                                   "stringAttr", "floatAttr");

List<Type> types = Arrays.asList(Type.boolType(), Type.intType(),
                                  Type.stringType(5), Type.floatType());

Schema s = new Schema(names, types);
```
Tables can be created as follows:
```
//creates a table with with schema s
db.createTable(s, "myTableName");

//creates a table with with schema s and builds an index on the intAttr field
db.createTableWithIndices(s, "myTableName", 
                           Arrays.asList("intAttr"));
```
The `QueryPlan` interface allows you to generate SQL-like queries without having to parse actual SQL queries:
```java

/**
* SELECT * FROM myTableName WHERE stringAttr = 'CS 186'
*/

// create a new transaction
Database.Transaction transaction = db.beginTransaction();


// add a join and a select to the QueryPlan
QueryPlan query = transaction.query("myTableName");
query.select("stringAttr", PredicateOperator.EQUALS, "CS 186");

// execute the query and get the output
Iterator<Record> queryOutput = query.executeOptimal();
```
To consider a more complicated example:
```java

/**
* SELECT * 
* FROM Students as S, Enrollment as E
* WHERE E.sid = S.sid AND
*       E.cid = 'CS 186' 
*/

// create a new transaction
Database.Transaction transaction = this.database.beginTransaction();

// alias both the Students and Enrollments tables
transaction.queryAs("Students", "S");
transaction.queryAs("Enrollments", "E");

// add a join and a select to the QueryPlan
QueryPlan query = transaction.query("S");
query.join("E", "S.sid", "E.sid");
query.select("E.cid", PredicateOperator.EQUALS, "CS 186");

// execute the query and get the output
Iterator<Record> queryOutput = query.executeOptimal();
```
`query.join` specifies an equality join. Its first argument is one of the two relations to be joined; the remaining two arguments are a key from the left table and the right table to join.

Note the `executeOptimal()` method above, this returns and executes optimal query plan. To see what this query plan is, you can print the query operator object:
```
// assuming query.executeOptimal() has already been called as above
QueryOperator finalOperator = query.getFinalOperator();
System.out.println(finalOperator.toString());



type: BNLJ
leftColumn: S.sid
rightColumn: E.sid
    (left)
    type: WHERE
    column: E.cid
    predicate: EQUALS
    value: CS 186
        type: SEQSCAN
        table: E

    (right)
    type: SEQSCAN
    table: S
```

In summary, if you would like to run queries on the database, you can create a new `QueryPlan` by calling `Transaction#query` and passing the name of the base table for the query. You can then call the `QueryPlan#select`, `QueryPlan#join`, etc. methods in order to generate as simple or as complex a query as you would like. Finally, call `QueryPlan#executeOptimal` to run the query optimizer,  execute the query, and get a response of the form `Iterator<Record>`. You can also use the `Transaction#queryAs` methods to alias tables.

## Your Assignment

Alright, now you are ready to write some code! **NOTE**: Throughout this project, you're free to add any helper methods you'd like to write. However, it is very important that you **do not change any of the interfaces that we've given you**. 

It's also a good idea to always check the course repo for updates on the project.

### Part 1: Cost Estimation and Maintenance of Statistics
The first part of building the query optimizer is ensuring that each query operator has the appropriate IO cost estimates. In order to estimate IO costs for each query operator, you will need the table statistics for any input operators. This information is accessible from the `QueryOperator#getStats` method. The `TableStats` object returned represents estimated statistics of the operator's output, including information such as number of tuples and number of pages in the output among others. These statistics are generated whenever a `QueryOperator` is constructed.

**NOTE** that these statistics are meant to be approximate so please pay careful attention to how we define the quantities to track.

We will use histograms to track table statistics. A histogram maintains approximate statistics about a (potentially large) set of values without explicitly storing the values. 
A histogram is an ordered list of B "buckets", each of which defines a range \[low, high). For the first, B - 1 buckets, the low of the range is inclusive and the high of the range is exclusive. **Exception**: For the last Bucket the high of the range is inclusive as well.  Each bucket counts the number of values and distinct values that fall within its range:
```java
Bucket<Float> b = new Bucket(10.0, 100.0); //defines a bucket whose low value is 10 and high is 100
b.getStart(); //returns 10.0
b.getEnd(); //returns 100.0
b.increment(15);// adds the value 15 to the bucket
b.getCount();//returns the number of items added to the bucket
b.getDistinctCount();//returns the approximate number of distinct iterms added to the bucket
```

In our implementation, the `Histogram` class, you will work with a floating point histogram where low and high are defined by floats. All other data types are backed by this floating point histogram through a "quantization" function `Histogram#quantization`. The histogram tracks statistics for all the values in a column of a table; we also need to support filtering the histogram based on given predicates.  After implementing all the methods below, you should be passing all of the tests in `TestHistogram`.

#### 1.1 
Write the `Histogram#buildHistograms` method. Given a table and an attribute, this method initializes the buckets and sets them to an initial count based on the data in the table. You will need to use the appropriate `Bucket` methods to do this--see the comments inside the method. 

#### 1.2 
The `Histogram#filter` method is given a predicate and returns a multiplicative mask for the histogram. That is, an array of size `numBuckets` where each entry is a float between 0 and 1 that represents a scaling factor to use in updating the histogram count. `Histogram#filter` takes in a predicate operator (<,>,=,!=, >=, <=), and a `DataBox` value to compare and returns the mask. Write the `Histogram#allEquality` , `Histogram#allNotEquality`, `Histogram#allGreaterThan`, and `Histogram#allLessThan` methods. For all data types, these methods filter the histogram. They return a multiplicative mask for the specific predicate operator.


### Part 2 Query Optimization
Before you get started, you should take a look at the different provided constructors and methods in `QueryPlan`. This will give you an idea of the components of the query that you have to optimize. Pay close attention to the `QueryOperator` class, whose sub-classes implement the physical operators that actually answer the query. 

To implement the single-table example in the previous part with a sequential scan:
```java
/**
* SELECT * FROM myTableName WHERE stringAttr = 'CS 186'
*/
QueryOperator source = SequentialScanOperator(transaction, myTableName);
QueryOperator select = SelectOperator(source, 'stringAttr', PredicateOperator.EQUALS, "CS 186");

select.iterator() //iterator over the results
```

To implement the join example in the previous part with a sequential scan and a block nested loop join:
```
/**
* SELECT * 
* FROM Students as S, Enrollment as E
* WHERE E.sid = S.sid AND
*       E.cid = 'CS 186' 
*/
QueryOperator s = SequentialScanOperator(transaction, 'Students');
QueryOperator e = SequentialScanOperator(transaction, 'Enrollment');

QueryOperator e186 = SelectOperator(e, 'cid', PredicateOperator.EQUALS, "CS 186");

BNLJOperator bjoin = BNLJOperator(s, e186, 'S.sid','E.sid', transaction);

bjoin.iterator() //iterator over the results
```
This defines a tree of `QueryOperator` objects, and `QueryPlan` finds such a tree to minimize I/O cost. Each `QueryOperator` has two relevant methods `estimateIOCost()` (which returns an estimated IO cost based on any stored statistics) and `iterator()` (which returns a iterator over the result tuples).

#### 2.1 System R Dynamic Programming
`QueryPlan#executeOptimal()` provides the scaffolding for the System R dynamic programming search algorithm. It constructs the optimal tree of operators and then returns the result iterator.

#### 2.1.1 Single Table Access Selection (Pass 1)
The first part of the search algorithm involves finding the lowest cost plans for accessing each individual table reference in the query. You will be implementing this functionality in `QueryPlan#minCostSingleAccess`. There are two possible scan operators you can use a `SequentialScanOperator` or `IndexScanOperator`. You should first calculate the estimated IO cost of performing a sequential scan. Then, if there are any eligible indices that can be used to scan the table, it should calculate the estimated IO cost of performing such an index scan. The `QueryPlan#getEligibleIndexColumns` method can be used to determine whether there are any existing indices that can be used for this query. This returns the set of columns on which there exists a index (independent of what query you are running). If the `IndexScanOperator` has a strictly lower cost, use the `IndexScanOperator` instead.

Then, as part of a heuristic-based optimization we covered in class, you should push down any selections that correspond to the table. You should be applying predicates as soon as they are eligible during bottom-up enumeration `QueryPlan#addEligibleSelections` which will be called by the `QueryPlan#minCostSingleAccess` method. See the comment in the code for details.

The end result of this method should be a query operator that starts with either a `SequentialScanOperator` or `IndexScanOperator` followed by zero or more `SelectOperator`'s.
After implementing all the methods up to this point, you should be passing all of the tests in `TestSingleAccess`. These tests do not involve any joins.

Returning our attention to `QueryPlan#executeOptimal()`, the result is put into a map structure, which maps each table name to its lowest cost operator. Each table is represented as a singleton set, which will be the input for the next stage of the algorithm.

#### 2.1.2 Join Algorithms (Pass i > 1)
The next part of the search algorithm involves finding the lowest cost join between each set of tables formed in the previous pass and a separate single table. You will be implementing this functionality in `QueryPlan#minCostJoins`. This method takes a map in a map of left-deep plans on $i$ relations and should produce a map of left-deep plans on $i+1$ relations. All subsets of $i+1$ should have an entry in the map be included unless they involved cartesian products. Use the list of explicit join conditions added through the `QueryPlan#join` method to identify potential joins. The end result of this method should be a mapping from a set of tables to a join query operator that corresponds to the lowest cost join estimated. 

#### 2.1.3 Optimal Plan Selection

Your final task is to write the outermost driver method of the optimizer, `QueryPlan#executeOptimal`. This method should invoke the various passes of the Selinger dynamic programming algorithm, and in the end return the optimal plan for the full set of tables. You first have to first find the optimal single table access plan for all the individual tables that you want to join, and then recursively use `QueryPlan#minCostJoins` to find the best joins between tables until all tables have been joined together. Finally, you have to add the remaining groupBy and project operators that are part of the query but have not been added to the query plan so far.  After implementing all the methods up to this point, you should be passing all of the tests in `TestOptimizationJoins` and `TestBasicQuery`.
 
### Submitting the Assignment

After you complete the assignment, simply commit and git push your hw4 branch. 60% of your grade will come from passing the unit tests we provide to you. 40% of your grade will come from passing unit tests that we have not provided to you. If your code does not compile on the VM with maven, we reserve the right to give you a 0 on the assignment.

