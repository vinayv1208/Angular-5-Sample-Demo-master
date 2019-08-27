# bank-ocr kata

This project is a placeholder for practicing the Bank OCR kata.

## Getting started

The project is preconfigured to execute Cucumber tests with JUnit. You can run
the tests from the command line with:

```
$ mvn test
```

Or from Eclipse with `Run As` -> `Maven test`:

<img width="709" alt="screen shot 2016-07-27 at 1 28 36 pm" src="https://cloud.githubusercontent.com/assets/79303/17185214/36405dc4-53fe-11e6-9768-54735e01489c.png">

## Writing features

Feature files are stored in [src/test/resources/bank](src/test/resources/bank),
and you can add additional tests by adding additional `Scenario:` constructs to
the existing feature file or by adding additional feature files.

## Implementing step definitions

When you run `mvn test`, Cucumber will output method stubs of step definitions
to implement the tests like this:

```
@When("^I parse the file$")
public void i_parse_the_file() throws Throwable {
    // Write code here that turns the phrase above into concrete actions
    throw new PendingException();
}
```

Step definitions can be defined with the `@Given`, `@When`, and `@Then` method
annotations in any class you like, though the `src/test/java/bank/Stepdefs.java`
class has already been defined for you to start from.

## Resources

For more information, see:

* [Our wiki page on the kata](https://github.com/testdouble/contributing-tests/wiki/Bank-OCR-Kata-in-Cucumber)
* [The kata's home page](http://www.codingdojo.org/cgi-bin/index.pl?KataBankOCR)
