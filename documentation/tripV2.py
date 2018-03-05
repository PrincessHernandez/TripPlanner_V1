#!/usr/bin/python
import os
import glob
import pygame, sys
import time
from time import *
import serial

os.system('modprobe w1-gpio')
os.system('modprobe w1-therm')

base_dir = '/sys/bus/w1/devices'
#device_folder = glob.glob(base_dir + '28*')[0]
#device_file = device_folder + '/w1_slave'

def read_temp_raw():
	f = open(device_file, 'r')
	lines = f.readlines()
	f.close
	return lines

def read_temp():
	lines = read_temp_raw()
	while lines[0].strip()[-3:] != 'YES':
		time.sleep(0.2)
		lines = read_temp_raw()
	equals_pos = lines[1].find('t=')
	if equals_pos != -1:
		temp_string = lines[1][equals_pos+2:]
		temp_c = float(temp_string) / 1000.0
		return temp_c

    #initialise serial port on /ttyUSB0
ser = serial.Serial('/dev/ttyUSB0',4800,timeout = None)

fix = 1

x = 0
while x == 0:
   gps = ser.readline()
   # print all NMEA strings
   print gps
   # check gps fix status
   if gps[1:6] == "GPGSA":
      fix = int(gps[9:10])
   # print time, lat and long from #GPGGA string
   if gps[1 : 6] == "GPGGA":
       # get time
       #time = gps[7:9] + ":" + gps[9:11] + ":" + gps[11:13]
       # if 2 or 3D fix get lat and long
       if fix > 1:
          lat = " " + gps[18:20] + "." + gps[20:22] + "." + gps[23:27] + gps[28:29]
          lon = " " + gps[30:33] + "." + gps[33:35] + "." + gps[36:40] + gps[41:42]
       # if no fix
       else:
          lat = " No Valid Data "
          lon = " "
