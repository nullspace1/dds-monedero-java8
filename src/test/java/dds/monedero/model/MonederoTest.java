package dds.monedero.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoInvalidoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MonederoTest {
  private Cuenta cuenta;

  @BeforeEach
  void init() {
    Movimiento.setClock(Clock.systemDefaultZone());
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
    cuenta.poner(15);
    cuenta.poner(4);
    cuenta.poner(19);
    assertThrows(MaximaCantidadDepositosException.class, () ->
      cuenta.poner(24));
  }

  @Test
  void ExtraerMasQueElSaldoFalla() {
    assertThrows(SaldoMenorException.class, () -> {
      cuenta.setSaldo(90);
      cuenta.sacar(101);
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
  public void limiteDeExtraccionesSeReseteaAlTerminarElDia(){
    cuenta.setSaldo(100);
    Clock unaFecha = Clock.fixed(Instant.parse("2022-04-29T00:02:00.00Z"), ZoneId.of("America/Argentina/Buenos_Aires"));
    Clock otraFecha = Clock.fixed(Instant.parse("2022-04-30T00:02:00.00Z"), ZoneId.of("America/Argentina/Buenos_Aires"));

    Movimiento.setClock(unaFecha);
    cuenta.sacar(10);
    cuenta.sacar(10);
    cuenta.sacar(10);
    Movimiento.setClock(otraFecha);
    Assertions.assertDoesNotThrow(() -> cuenta.sacar(10));
  }

  @Test
  public void limiteCantidadDeDepositosSeReseteaAlTerminarElDia(){
    Clock unaFecha = Clock.fixed(Instant.parse("2022-04-29T00:02:00.00Z"), ZoneId.of("America/Argentina/Buenos_Aires"));
    Clock otraFecha = Clock.fixed(Instant.parse("2022-04-30T00:02:00.00Z"), ZoneId.of("America/Argentina/Buenos_Aires"));

    Movimiento.setClock(unaFecha);
    cuenta.poner(1000);
    Movimiento.setClock(otraFecha);
    Assertions.assertDoesNotThrow(() -> cuenta.poner(1));
  }







}
