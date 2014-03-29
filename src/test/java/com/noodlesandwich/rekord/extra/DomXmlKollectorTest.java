package com.noodlesandwich.rekord.extra;

import com.google.common.base.Joiner;
import com.noodlesandwich.rekord.Key;
import com.noodlesandwich.rekord.Rekord;
import org.junit.Test;
import org.w3c.dom.Document;

import static com.noodlesandwich.rekord.testobjects.Rekords.Address;
import static com.noodlesandwich.rekord.testobjects.Rekords.Person;
import static com.noodlesandwich.rekord.testobjects.Rekords.Sandvich;
import static com.noodlesandwich.rekord.testobjects.Rekords.Sandvich.Bread.Brown;
import static com.noodlesandwich.rekord.testobjects.Rekords.Sandvich.Filling.Cheese;
import static com.noodlesandwich.rekord.testobjects.Rekords.Sandvich.Style.Roll;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.xmlmatchers.XmlMatchers.isSimilarTo;
import static org.xmlmatchers.transform.XmlConverters.the;

public final class DomXmlKollectorTest {
    @Test public void
    a_rekord_with_one_element_is_kollected_into_XML() {
        Rekord<Sandvich> sandvich = Sandvich.rekord
                .with(Sandvich.filling, Cheese);

        Document document = sandvich.collect(new DomXmlKollector("sandvich"));

        assertThat(the(document), isSimilarTo(the(lines(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                "<sandvich>",
                "    <filling>Cheese</filling>",
                "</sandvich>"
        ))));
    }
    @Test public void
    a_rekord_with_multiple_elements_is_kollected_into_XML() {
        Rekord<Sandvich> sandvich = Sandvich.rekord
                .with(Sandvich.bread, Brown)
                .with(Sandvich.filling, Cheese)
                .with(Sandvich.style, Roll);

        Document document = sandvich.collect(new DomXmlKollector("sandvich"));

        assertThat(the(document), isSimilarTo(the(lines(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                "<sandvich>",
                "    <bread>Brown</bread>",
                "    <filling>Cheese</filling>",
                "    <style>Roll</style>",
                "</sandvich>"
        ))));
    }

    @Test public void
    rekord_and_key_names_are_lowercased_and_slugified_into_valid_XML_element_names() {
        Key<Person, Integer> key_12345 = Key.named("12345");
        Key<Person, String> key_up = Key.named("^up");

        Rekord<Person> modifiedPersonRekord = Rekord.of(Person.class)
                .accepting(Person.firstName, Person.lastName, Person.age, key_12345, key_up);

        Rekord<Person> spongebob = modifiedPersonRekord
                .with(Person.firstName, "Spongebob")
                .with(Person.lastName, "Squarepants")
                .with(Person.age, 27)
                .with(key_12345, 67890)
                .with(key_up, "down");

        Document document = spongebob.collect(new DomXmlKollector("sea creaTURE"));

        assertThat(the(document), isSimilarTo(the(lines(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                "<sea-creature>",
                "    <first-name>Spongebob</first-name>",
                "    <last-name>Squarepants</last-name>",
                "    <age>27</age>",
                "    <_12345>67890</_12345>",
                "    <_up>down</_up>",
                "</sea-creature>"
        ))));
    }

    @Test public void
    a_rekord_with_nested_rekords_is_kollected_into_nested_XML() {
        Rekord<Person> person = Person.rekord
                .with(Person.firstName, "Philip")
                .with(Person.lastName, "Sherman")
                .with(Person.address, Address.rekord
                        .with(Address.houseNumber, 42)
                        .with(Address.street, "Wallaby Way")
                        .with(Address.city, "Sydney"));

        Document document = person.collect(new DomXmlKollector("person"));

        assertThat(the(document), isSimilarTo(the(lines(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                "<person>",
                "    <first-name>Philip</first-name>",
                "    <last-name>Sherman</last-name>",
                "    <address>",
                "        <house-number>42</house-number>",
                "        <street>Wallaby Way</street>",
                "        <city>Sydney</city>",
                "    </address>",
                "</person>"
        ))));
    }

    private static String lines(String... lines) {
        return Joiner.on('\n').join(lines);
    }
}
