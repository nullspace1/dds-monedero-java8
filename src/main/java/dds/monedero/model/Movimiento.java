package dds.monedero.model;

import java.time.LocalDate;

public class Movimiento {

  private LocalDate fecha;
  private double monto;

  public Movimiento(double monto) {
    this.fecha = LocalDate.now();
    this.monto = monto;
  }

  public double getMonto() {
    return monto;
  }

  public LocalDate getFecha() {
    return fecha;
  }

  public boolean esDeLaFecha(LocalDate fecha) {
    return this.fecha.equals(fecha);
  }

}
