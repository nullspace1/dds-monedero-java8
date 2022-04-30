package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Para cuando alguien vea esto, los comentarios se suponen que los voy a borrar antes de la entrega
// final.
// Si aun estan, perdon :c

public class Cuenta {

  private double saldo;
  private double limiteExtraccion = 1000;
  private int maximaCantidadDeDepositos;

  private List<Movimiento> movimientos = new ArrayList<>();

  public Cuenta(double montoInicial) {
    saldo = montoInicial;
  }

  public void poner(double cantidadDepositada) {
    verificarSePuedeDepositar(cantidadDepositada);
    agregarMovimiento(cantidadDepositada, TipoMovimiento.DEPOSITO);
    saldo += cantidadDepositada;
  }


  public void sacar(double cantidadExtraida) {
    verificarSePuedeExtraer(cantidadExtraida);
    agregarMovimiento(cantidadExtraida, TipoMovimiento.EXTRACCION);
    saldo -= cantidadExtraida;
  }


  public void agregarMovimiento(double cuanto, TipoMovimiento tipo) {
    Movimiento movimiento = new Movimiento(cuanto, tipo);
    movimientos.add(movimiento);
  }

  public double getMontoExtraidoA(LocalDate fecha) {
    return movimientos.stream()
        .filter(movimiento -> !movimiento.isDeposito() && movimiento.getFecha().equals(fecha))
        .mapToDouble(Movimiento::getMonto).sum();
  }

  public long cantidadDeDepositosEnElDia() {
    return movimientos.stream().filter(movimiento -> movimiento.isDeposito()).count();
  }

  public boolean saldoInsuficiente(double cantidadExtraida) {
    return getSaldo() - cantidadExtraida < 0;
  }

  public boolean superaLimiteExtraccion(double cantidadExtraida) {
    return limiteExtraccion() - cantidadExtraida < 0;
  }

  public double limiteExtraccion() {
    double montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    return this.limiteExtraccion - montoExtraidoHoy;
  }

  private void verificarSePuedeDepositar(double cantidadDepositada) {
    if (cantidadDepositada <= 0) {
      throw new MontoNegativoException(
          cantidadDepositada + ": el monto a ingresar debe ser un valor positivo");
    }

    // Metemos el comportamiento de calcular la cantidad de depositos en el dia en un metodo propio
    if (cantidadDeDepositosEnElDia() >= this.maximaCantidadDeDepositos) {
      throw new MaximaCantidadDepositosException(
          "Ya excedio los " + this.maximaCantidadDeDepositos + " depositos diarios");
    }
  }

  private void verificarSePuedeExtraer(double cantidadExtraida) {
    if (cantidadExtraida <= 0) {
      throw new MontoNegativoException(
          cantidadExtraida + ": el monto a ingresar debe ser un valor positivo");
    }
    if (saldoInsuficiente(cantidadExtraida)) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }

    if (superaLimiteExtraccion(cantidadExtraida)) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + this.limiteExtraccion
          + " diarios, límite: " + limiteExtraccion());
    }

  }


  public double getSaldo() {
    return saldo;
  }

  public void setSaldo(double saldo) {
    this.saldo = saldo;
  }



}
