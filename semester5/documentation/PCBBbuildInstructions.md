The TripPi device must include two custom printed circuit boards. One is built to drive the stepper <br>
motor, and the other is to have the temperature sensor connected neatly to the raspberry PI. The materials <br>
needed for the temperature PCB include: <br>

*Temperature sensor <br>
4700 ohm resistor <br>
20 pin header <br>

To get started, the temperature sensor PCB is simpler to create as it only has three traces. The first/red <br>
wire trace should connect the power wire to a 3.3V pin on the rasberry pi. The second/black wire trace should <br>
connect to one of the ground pins on the raspberry pi. The third/yellow wire trace should lead to a GPIO pin <br>
on the raspberry pi. Any pin will do as long as it does not interfere with another GPIO pin in use by another <br>
lead. The GPIO pin we used in this example is GPIO4. On this PCB, a 4700 ohm resistor must be connected between <br>
the power trace and the GPIO trace, without touching the ground trace. The board itself only needs to be about <br>
half the size of a full size pi board which connects to all 40 pins, as this one does not need to use most of <br>
the pins. 

[Insert cool image of temp PCB here]

[Insert more useful image of temp PCB here]

The second printed circuit board, which is used to drive the stepper motor, will be more complicated to create <br>
due to many overlapping traces, and the use of double the pins tha the temperature sensor PCB uses. The materials <br>
needed for this printed circuit board are: <br>

L293D IC Chip <br>
5 pin header <br>
40 pin GPIO header <br>
16 Pin IC socket <br>

What the L293D IC chip contains is the capacitors and smaller components needed to drive the motor, all contained <br>
into the one IC chip. The board can still be made without the IC chip but would need many more components and would <br>
be more time consuming to create. Generally, the PCB will contain 7 traces connecting the raspberry pi pins <br>
to the different components on the printed circuit board. The traces will be connected as described in the following <br>
L293D IC pinout. <br>

[insert L293D pinout here]


As shown, the pins on the chip are numbered as they are on common IC chips. To get the general idea, pin 16 on the <br>
IC must be connected to the 5V power pin on the raspberry pi, and pin 16 must be connected to pin 8, as both sides <br>
of the chip must be powered in order to work. Next, 
