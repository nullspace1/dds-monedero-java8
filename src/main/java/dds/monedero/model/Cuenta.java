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

  private List<Movimiento> movimientos = new ArrayList<>();

  public Cuenta(double montoInicial) {
    saldo = montoInicial;
  }

  public void poner(double cantidadDepositada) {

    if (cantidadDepositada <= 0) {
      throw new MontoNegativoException(
          cantidadDepositada + ": el monto a ingresar debe ser un valor positivo");
    }

    // Metemos el comportamiento de calcular la cantidad de depositos en el dia en un metodo propio
    if (cantidadDeDepositosEnElDia() >= 3) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }

    // Aca teniamos Primitive Obsession con el booleano para saber si era movimiento, y por otro
    // lado la clase de movimiento se encargaba de mandarle un mensaje para que cargue el movimiento
    // en la lista (??)

    // Tambien no era necesario pasarle la fecha al movimiento como parametro del constructor, el
    // movimiento lo puede hacer por si solo

    Movimiento movimientoRealizado = new Movimiento(cantidadDepositada, TipoMovimiento.DEPOSITO);
    movimientos.add(movimientoRealizado);
    saldo += cantidadDepositada;
  }

  public void sacar(double cantidadExtraida) {

    if (cantidadExtraida <= 0) {
      throw new MontoNegativoException(
          cantidadExtraida + ": el monto a ingresar debe ser un valor positivo");
    }
    if (saldoInsuficiente(cantidadExtraida)) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }

    if (superaLimiteExtraccion(cantidadExtraida)) {
      throw new MaximoExtraccionDiarioException(
          "No puede extraer mas de $ " + 1000 + " diarios, límite: " + limiteExtraccion());
    }

    Movimiento movimientoRealizado = new Movimiento(cantidadExtraida, TipoMovimiento.DEPOSITO);
    movimientos.add(movimientoRealizado);
    saldo -= cantidadExtraida;
  }

  public void agregarMovimiento(LocalDate fecha, double cuanto, boolean esDeposito) {
    Movimiento movimiento = new Movimiento(fecha, cuanto, esDeposito);
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
    return 1000 - montoExtraidoHoy;
  }

  public double getSaldo() {
    return saldo;
  }

  public void setSaldo(double saldo) {
    this.saldo = saldo;
  }



}
