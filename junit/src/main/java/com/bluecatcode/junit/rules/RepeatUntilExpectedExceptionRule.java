package com.bluecatcode.junit.rules;

import org.hamcrest.Matcher;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import static com.bluecatcode.junit.rules.RepeatRule.RepeatStatement.createFromAnnotationWith;

public class RepeatUntilExpectedExceptionRule implements TestRule {

    private ExpectedException expectedException = ExpectedException.none();

    @Override
    public Statement apply(Statement statement, Description description) {
        Repeat repeat = description.getAnnotation(Repeat.class);
        Statement statement1 = createFromAnnotationWith(repeat, statement);
        Statement resultStatement = statement1 == null ? statement : statement1;
        return expectedException.apply(resultStatement, description);
    }

    public void expectMessage(Matcher<String> matcher) {
        expectedException.expectMessage(matcher);
    }

    public void expect(Class<? extends Throwable> type) {
        expectedException.expect(type);
    }

    public void expect(Matcher<?> matcher) {
        expectedException.expect(matcher);
    }

    public void expectMessage(String substring) {
        expectedException.expectMessage(substring);
    }

}
