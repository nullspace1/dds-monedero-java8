package dds.monedero.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoInvalidoException;
import dds.monedero.exceptions.SaldoMenorException;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MonederoTest {
  private Cuenta cuenta;

  @BeforeEach
  void init() {
    cuenta = new Cuenta(0);
  }

  @Test
  void Poner() {
    cuenta.poner(1500);
    assertEquals(1500, cuenta.getSaldo());
  }

  @Test
  void PonerDepositoNegativoFalla() {
    assertThrows(MontoInvalidoException.class, () -> cuenta.poner(-1500));
  }

  @Test
  void PonerDepositoNuloFalla() {
    assertThrows(MontoInvalidoException.class, () -> cuenta.poner(0));
  }

  @Test
  void PonerExtraccionNegativaFalla() {
    assertThrows(MontoInvalidoException.class, () -> cuenta.sacar(-1500));
  }

  @Test
  void PonerExtraccionNulaFalla() {
    assertThrows(MontoInvalidoException.class, () -> cuenta.sacar(0));
  }

  @Test
  void ExtraerMasDe3VecesFalla() {
    assertThrows(MaximaCantidadDepositosException.class, () -> {
      cuenta.poner(1500);
      cuenta.poner(456);
      cuenta.poner(1900);
      cuenta.poner(245);
    });
  }

  @Test
  void ExtraerMasQueElSaldoFalla() {
    assertThrows(SaldoMenorException.class, () -> {
      cuenta.setSaldo(90);
      cuenta.sacar(1001);
    });
  }

  @Test
  void ExtraerTodo() {
    cuenta.setSaldo(900);
    cuenta.sacar(900);
    assertEquals(0, cuenta.getSaldo());
  }


  @Test
  public void ExtraerMasDe1000Falla() {
    assertThrows(MaximoExtraccionDiarioException.class, () -> {
      cuenta.setSaldo(5000);
      cuenta.sacar(1001);
    });
  }


  @Test
  public void obtenerExtraccionesEnFecha() {
    cuenta.setSaldo(900);
    cuenta.sacar(400);
    cuenta.sacar(200);
    assertEquals(600, cuenta.getMontoExtraidoA(LocalDate.now()));
  }

  @Test
  public void cantidadDepositosEnFecha() {
    cuenta.poner(100);
    cuenta.poner(100);
    assertEquals(2, cuenta.cantidadDeDepositosEnElDia(LocalDate.now()));
  }

  // Me gustaria poner mas tests con la fecha, pero por ahora como no puedo poner operaciones en
  // fechas diferentes queda ahi.



}
