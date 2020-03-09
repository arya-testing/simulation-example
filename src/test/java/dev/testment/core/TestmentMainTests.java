package dev.testment.core;

import dev.testment.core.application.exceptions.AmbiguousApplicationException;
import dev.testment.core.application.exceptions.ApplicationNotFoundException;
import dev.testment.core.fixtures.testmentmaintests.NamespaceA;
import dev.testment.core.fixtures.testmentmaintests.NamespaceB;
import dev.testment.core.fixtures.testmentmaintests.NamespaceC;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

public class TestmentMainTests {

    @Test
    public void testMain() {
        TestmentMain.main(new String[]{"--scan-prefix", NamespaceA.class.getName()});
        assertThat(NamespaceA.Application.calledMain).isTrue();
    }

    @Test
    public void testFailToRunMainWithoutApplicationCandidate() {
        ApplicationNotFoundException ex = assertThrows(ApplicationNotFoundException.class, () -> TestmentMain.main(new String[]{"--scan-prefix", NamespaceB.class.getName()}));
        assertThat(ex).hasMessageContaining("No TestmentApplication found!");
    }

    @Test
    public void testFailToRunMainWithMultipleApplicationCandidates() {
        AmbiguousApplicationException ex = assertThrows(AmbiguousApplicationException.class, () -> TestmentMain.main(new String[]{"--scan-prefix", NamespaceC.class.getName()}));
        assertThat(ex).hasMessageContaining("Found 2 TestmentApplication candidates");
    }

}