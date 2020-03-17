package com.intuit.fuzzymatcher.component;

import com.intuit.fuzzymatcher.domain.Element;
import com.intuit.fuzzymatcher.domain.ElementType;
import com.intuit.fuzzymatcher.domain.MatchType;
import com.intuit.fuzzymatcher.domain.Token;
import com.intuit.fuzzymatcher.exception.MatchException;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class TokenRepoTest {

    @Test
    public void shouldGetForNameWithEquality() {
        List<Object> names = Arrays.asList("Amy Doe", "Brian Doe", "Jane Amy", "Michael Wane");

        List<Element> elements = getElements(names, ElementType.NAME, null);

        TokenRepo tokenRepo = new TokenRepo();

        elements.forEach(element -> {
            Stream<Token> tokenStream = element.getTokens();
            tokenStream.forEach(token -> tokenRepo.put(token));
        });

        Element testElement1 = new Element.Builder().setType(ElementType.NAME).setValue("Amy").createElement();
        Token token1 = testElement1.getTokens().findFirst().get();
        Set<Element> matchingElements1 = tokenRepo.get(token1);
        Assert.assertTrue(matchingElements1.contains(elements.get(0)));
        Assert.assertTrue(matchingElements1.contains(elements.get(2)));

        Element testElement2 = new Element.Builder().setType(ElementType.NAME).setValue("Doe").createElement();
        Token token2 = testElement2.getTokens().findFirst().get();
        Set<Element> matchingElements2 = tokenRepo.get(token2);
        Assert.assertTrue(matchingElements2.contains(elements.get(0)));
        Assert.assertTrue(matchingElements2.contains(elements.get(1)));
    }

    @Test
    public void shouldGetForNumberWithNearestNeighbour() {
        List<Object> numbers = Arrays.asList(100, 200, 1, 25, 700, 99, 210, 500);

        List<Element> elements = getElements(numbers, ElementType.NUMBER, null);

        TokenRepo tokenRepo = new TokenRepo();

        elements.forEach(element -> {
            Stream<Token> tokenStream = element.getTokens();
            tokenStream.forEach(token -> tokenRepo.put(token));
        });

        Element testElement1 = new Element.Builder().setType(ElementType.NUMBER).setValue(101).createElement();
        Token token1 = testElement1.getTokens().findFirst().get();
        Set<Element> matchingElements1 = tokenRepo.get(token1);
        Assert.assertTrue(matchingElements1.contains(elements.get(0)));
        Assert.assertTrue(matchingElements1.contains(elements.get(5)));

        Element testElement2 = new Element.Builder().setType(ElementType.NUMBER).setValue(205).createElement();
        Token token2 = testElement2.getTokens().findFirst().get();
        Set<Element> matchingElements2 = tokenRepo.get(token2);
        Assert.assertTrue(matchingElements2.contains(elements.get(1)));
        Assert.assertTrue(matchingElements2.contains(elements.get(6)));
    }

    @Test
    public void shouldGetForNumberWithEquality() {
        List<Object> numbers = Arrays.asList(100, 200, 1, 25, 700, 99, 210, 500);

        List<Element> elements = getElements(numbers, ElementType.NUMBER, MatchType.EQUALITY);

        TokenRepo tokenRepo = new TokenRepo();

        elements.forEach(element -> {
            Stream<Token> tokenStream = element.getTokens();
            tokenStream.forEach(token -> tokenRepo.put(token));
        });

        Element testElement1 = new Element.Builder().setType(ElementType.NUMBER).setValue(100).createElement();
        Token token1 = testElement1.getTokens().findFirst().get();
        Set<Element> matchingElements1 = tokenRepo.get(token1);
        Assert.assertTrue(matchingElements1.contains(elements.get(0)));

        Element testElement2 = new Element.Builder().setType(ElementType.NUMBER).setValue(200).createElement();
        Token token2 = testElement2.getTokens().findFirst().get();
        Set<Element> matchingElements2 = tokenRepo.get(token2);
        Assert.assertTrue(matchingElements2.contains(elements.get(1)));
    }

    @Test(expected = MatchException.class)
    public void shouldGetForNotSupportedWithNearestNeighbour() {
        List<Object> numbers = Arrays.asList("100", "200", "1", "25", "700", "99", "210", "500");

        List<Element> elements = getElements(numbers, ElementType.TEXT, MatchType.NEAREST_NEIGHBOURS);

        TokenRepo tokenRepo = new TokenRepo();

        elements.forEach(element -> {
            Stream<Token> tokenStream = element.getTokens();
            tokenStream.forEach(token -> tokenRepo.put(token));
        });

        Element testElement1 = new Element.Builder().setType(ElementType.TEXT).setValue("101").createElement();
        Token token1 = testElement1.getTokens().findFirst().get();
        tokenRepo.get(token1);
    }

    private List<Element> getElements(List<Object> values, ElementType elementType, MatchType matchType) {
        return values.stream()
                .map(value -> {
                    Element.Builder elementBuilder = new Element.Builder().setType(elementType).setValue(value);
                    if (matchType != null) {
                        elementBuilder.setMatchType(matchType);
                    }
                    return elementBuilder.createElement();
                }).collect(Collectors.toList());
    }
}
