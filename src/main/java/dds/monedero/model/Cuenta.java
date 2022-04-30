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

  public void poner(double cuanto) {

    if (cuanto <= 0) {
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
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

    Movimiento movimientoRealizado = new Movimiento(cuanto, TipoMovimiento.DEPOSITO);
    movimientos.add(movimientoRealizado);
    saldo += cuanto;
  }

  public void sacar(double cuanto) {
    if (cuanto <= 0) {
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
    }
    if (getSaldo() - cuanto < 0) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }
    double montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    double limite = 1000 - montoExtraidoHoy;
    if (cuanto > limite) {
      throw new MaximoExtraccionDiarioException(
          "No puede extraer mas de $ " + 1000 + " diarios, límite: " + limite);
    }
    new Movimiento(LocalDate.now(), cuanto, false).agregateA(this);
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

  public double getSaldo() {
    return saldo;
  }

  public void setSaldo(double saldo) {
    this.saldo = saldo;
  }



}
