package dds.monedero.model;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;

public class Movimiento {

  private static Clock reloj = Clock.system(ZoneId.of("America/Argentina/Buenos_Aires"));
  private LocalDate fecha;
  private double monto;

  public Movimiento(double monto) {
    this.fecha = LocalDate.now(reloj);
    this.monto = monto;
  }

  public double getMonto() {
    return monto;
  }

  public LocalDate getFecha() {
    return fecha;
  }

  public boolean esDeLaFecha(LocalDate fecha) {
    return this.fecha.getYear() == fecha.getYear()
        && this.fecha.getDayOfYear() == fecha.getDayOfYear();
  }

  public static void setClock(Clock nuevoReloj){
    reloj = nuevoReloj;
  }

}
