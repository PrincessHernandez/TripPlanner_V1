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
the pins. This 20 pin header must also be connected so that the pins face upwards, and the sockets face downwards<br> 
to ensure the printed circuit board can fit nicely into the motor driving board.<br>

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
of the chip must be powered in order to work. There are four ground pins, but only one needs to be connected to the <br>
ground as all of the ground pins should be connected internally. Pins 2, 7, 10 and 15 are all input pins on the chip <br>
and need to be connected to different GPIO pins on the raspberry pi. After those are connected, pins 3, 6, 11 and 14 <br>
are to be connected to the four wires connected to the stepper motor. There is a certain order in which the pins <br>
should be connected, and the shown diagram  of the stepper printed circuit board is the most optimal way to connect <br>
it. Since this is a PCB, the motor will not be connected directly to it, for mainly for testing purposes and maintenance<br>
if needed. As such, this is where the five-pin header will be implemented. The pins that are to be connected to the <br>
motor from the L293D chip will instead be connected to this header so that the motor can simply be plugged into the <br>
header. Connecting the traces on the printed circuit board will seem difficult as there are many different components<br>
to connect, which would lead to some traces crossing over on one side. In these situations, there are holes surrounded<br>
by copper called "vias" which should be implemented in order to cross the traces to the other side of the printed<br>
circuit board. An important note is that the 40-pin header must be added to the printed circuit board in a way that <br>
the pins face upwards and the sockets face downwards. This is to ensure that the printed circuit board can "plug" <br>
into the raspberry Pi smoothly.
