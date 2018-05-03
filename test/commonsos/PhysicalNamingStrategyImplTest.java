package commonsos;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.jdbc.env.internal.JdbcEnvironmentImpl;
import org.hibernate.engine.jdbc.env.spi.IdentifierHelper;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class PhysicalNamingStrategyImplTest {

  PhysicalNamingStrategyImpl strategy = new PhysicalNamingStrategyImpl();
  JdbcEnvironment context = mock(JdbcEnvironmentImpl.class);
  IdentifierHelper identifierHelper = mock(IdentifierHelper.class);

  @Before
  public void setUp() throws Exception {
    when(context.getIdentifierHelper()).thenReturn(identifierHelper);
  }

  @Test
  public void toPhysicalTableNameConversion() {
    assertPhysicalTableName("testBest", "test_best");
    assertPhysicalTableName("testbest", "testbest");
    assertPhysicalTableName("Testbest", "testbest");
    assertPhysicalTableName("Test_Best", "test_best");
    assertPhysicalTableName("ABCDEFG", "ab_cd_ef_g");
  }

  private void assertPhysicalTableName(String original, String converted) {
    Identifier expectedResult = mock(Identifier.class);
    when(identifierHelper.toIdentifier(anyString(), anyBoolean())).thenReturn(expectedResult);

    Identifier result = strategy.toPhysicalTableName(new Identifier(original, false), context);

    assertThat(result).isSameAs(expectedResult);
    verify(identifierHelper).toIdentifier(converted, false);
    reset(identifierHelper);
  }

  @Test
  public void toPhysicalColumnName() {
    assertPhysicalColumnNameConversion("testBest", "test_best");
    assertPhysicalColumnNameConversion("testbest", "testbest");
    assertPhysicalColumnNameConversion("Testbest", "testbest");
    assertPhysicalColumnNameConversion("Test_Best", "test_best");
    assertPhysicalColumnNameConversion("ABCDEFG", "ab_cd_ef_g");
  }

  private void assertPhysicalColumnNameConversion(String original, String converted) {
    Identifier expectedResult = mock(Identifier.class);
    when(identifierHelper.toIdentifier(anyString(), anyBoolean())).thenReturn(expectedResult);

    Identifier result = strategy.toPhysicalColumnName(new Identifier(original, false), context);

    assertThat(result).isSameAs(expectedResult);
    verify(identifierHelper).toIdentifier(converted, false);
    reset(identifierHelper);
  }
}