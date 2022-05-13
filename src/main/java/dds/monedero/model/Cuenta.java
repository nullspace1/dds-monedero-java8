package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoInvalidoException;
import dds.monedero.exceptions.SaldoMenorException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {

  private double saldo;

  private final double LIMITE_EXTRACCION = 1000;
  private final int MAXIMA_CANTIDAD_DEPOSITOS = 3;
  private final List<Movimiento> extracciones = new ArrayList<>();
  private final List<Movimiento> depositos = new ArrayList<>();

  public Cuenta(double montoInicial) {
    saldo = montoInicial;
  }

  public void poner(double cantidadDepositada) {
    verificarSePuedeDepositar(cantidadDepositada);
    agregarDeposito(cantidadDepositada);
    saldo += cantidadDepositada;
  }

  public void sacar(double cantidadExtraida) {
    verificarSePuedeExtraer(cantidadExtraida);
    agregarExtraccion(cantidadExtraida);
    saldo -= cantidadExtraida;
  }

  private void verificarSePuedeDepositar(double cantidadDepositada) {
    if (cantidadDepositada <= 0) {
      throw new MontoInvalidoException(
          cantidadDepositada + ": el monto a ingresar debe ser un valor positivo");
    }

    if (cantidadDeDepositosEnElDia(LocalDate.now()) >= MAXIMA_CANTIDAD_DEPOSITOS) {
      throw new MaximaCantidadDepositosException(
          "Ya excedio los " + MAXIMA_CANTIDAD_DEPOSITOS + " depositos diarios");
    }
  }

  private void verificarSePuedeExtraer(double cantidadExtraida) {

    if (cantidadExtraida <= 0) {
      throw new MontoInvalidoException(
              cantidadExtraida + ": el monto a ingresar debe ser un valor positivo");
    }
    if (saldoInsuficiente(cantidadExtraida)) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }

    if (superaLimiteExtraccion(cantidadExtraida)) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + LIMITE_EXTRACCION
              + " diarios, límite: " + limiteExtraccion());
    }

  }

  public double montoExtraidoEnElDia(LocalDate fecha) {
    return extracciones.stream().filter(movimiento -> movimiento.esDeLaFecha(fecha))
            .mapToDouble(Movimiento::getMonto).sum();
  }

  public long cantidadDeDepositosEnElDia(LocalDate fecha) {
    return depositos.stream().filter(movimiento -> movimiento.esDeLaFecha(fecha)).count();
  }



  public void agregarDeposito(double cuanto) {
    Movimiento movimiento = new Movimiento(cuanto);
    depositos.add(movimiento);
  }

  public void agregarExtraccion(double cuanto) {
    Movimiento movimiento = new Movimiento(cuanto);
    extracciones.add(movimiento);
  }



  public boolean saldoInsuficiente(double cantidadExtraida) {
    return getSaldo() - cantidadExtraida < 0;
  }

  public boolean superaLimiteExtraccion(double cantidadExtraida) {
    return limiteExtraccion() - cantidadExtraida < 0;
  }

  public double limiteExtraccion() {
    double montoExtraidoHoy = montoExtraidoEnElDia(LocalDate.now());
    return LIMITE_EXTRACCION - montoExtraidoHoy;
  }

  public double getSaldo() {
    return saldo;
  }

  public void setSaldo(double saldo) {
    this.saldo = saldo;
  }



}
