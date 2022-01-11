#!/usr/bin/env python3

"""
   Analyses a CSV file of LoRaWAN gateway traffic.
"""

import argparse
import csv
from dateutil import parser as dateparser

# the net ids of the major operators, others will be counted as 'Other'
netids = {  0x00 : 'Experimental',
            0x03 : 'Proximus',
            0x0A : 'KPN',
            0x13 : 'TheThingsNetwork',
            0x24 : 'Helium',
            0x62 : 'Operator_62'}

def get_operator(row):
    """ takes a devaddr in hex and returns the operator it belongs to """
    dev_addr = row['dev_addr']
    if not dev_addr:
        return None
    netid = int(dev_addr, 16) >> 25
    return netids.get(netid, 'Other')

def read_csv(filename):
    """ reads a CSV file, returns a list of dicts, one for each packet """
    file = []
    with open(filename, encoding="utf-8") as csvfile:
        for row in csv.DictReader(csvfile, delimiter=',', quotechar='"'):
            file.append(row)
    return file

def get_datetime(row):
    """ extracts the time as a datetime """
    timestr = row['time']
    return dateparser.parse(timestr)

def analyse_frequency_use(packets):
    """ analyses the relative use of frequencies """
    timespan = (get_datetime(packets[-1]) - get_datetime(packets[0])).total_seconds()
    time_by_freq = {}
    for row in packets:
        airtime = float(row['airtime'])
        frequency = int(row['frequency'])
        time_by_freq[frequency] = time_by_freq.get(frequency, 0.0) + airtime
    print(f'\nAirtime by frequency: ({timespan:.0f} seconds total)')
    for freq,value in sorted(time_by_freq.items(), key=lambda item: item[0]):
        print(f'{freq:>12} Hz: {value / timespan:>5.1%} = {value:8.3f} sec')

def analyse_packet_types(packets):
    """ Analyses packets by type """

    # group airtime and total packets by operator
    count_by_type = {}
    time_by_type = {}
    count_total = 0
    time_total = 0
    for row in packets:
        pkt_type = row['type']
        count_by_type[pkt_type] = count_by_type.get(pkt_type, 0) + 1
        count_total += 1
        airtime = float(row['airtime'])
        time_by_type[pkt_type] = time_by_type.get(pkt_type, 0.0) + airtime
        time_total += airtime

    print(f'\nPacket types: ({count_total} packets total, {time_total:.3f} seconds total)')
    for pkt_type,count in sorted(count_by_type.items(), key=lambda item: item[1], reverse=True):
        airtime = time_by_type[pkt_type]
        print(f'{pkt_type:>20}: {count:>5d} ({count / count_total:5.1%} pkts, {airtime / time_total:5.1%} time)')

def analyse_unique_devices(packets):
    """ determines unique devices by operator """
    # create sets of unique devices, per operator
    dev_by_operator = {}
    for row in packets:
        operator = get_operator(row)
        if operator:
            devaddr = row['dev_addr']
            myset = dev_by_operator.get(operator, set())
            myset.add(devaddr)
            dev_by_operator[operator] = myset
    # convert set to count, per operator
    num_by_operator = {}
    num_total = 0
    for operator,deviceset in dev_by_operator.items():
        num_by_operator[operator] = len(deviceset)
        num_total += len(deviceset)
    print(f'\nUnique devices by operator: ({num_total} devices total)')
    for operator,num in sorted(num_by_operator.items(), key=lambda item: item[1], reverse=True):
        print(f'{operator:>20}: {num:>5}')

def analyse(packets):
    """ Analyses packets and prints the resuls to stdout """

    # group airtime and total packets by operator
    timedict = {}
    pktsdict = {}
    time_dev_total = 0
    pkts_dev_total = 0
    for row in packets:
        operator = get_operator(row)
        if operator:
            airtime = float(row['airtime'])
            timedict[operator] = timedict.get(operator, 0.0) + airtime
            pktsdict[operator] = pktsdict.get(operator, 0) + 1
            time_dev_total += airtime
            pkts_dev_total += 1

    print(f'\nAirtime by operator: ({time_dev_total:.3f} total)')
    for operator,value in sorted(timedict.items(), key=lambda item: item[1], reverse=True):
        print(f'{operator:>20}: {value / time_dev_total:>5.1%} = {value:.3f}')

    print(f'\nPacket # by operator: ({pkts_dev_total} total)')
    for operator,value in sorted(pktsdict.items(), key=lambda item: item[1], reverse=True):
        print(f'{operator:>20}: {value / pkts_dev_total:>5.1%} = {value}')

def main():
    """ The main entry point """
    parser = argparse.ArgumentParser()
    parser.add_argument("-f", "--filename", help="The name of the CSV file to analyse",
                        default="gateway.csv")
    args = parser.parse_args()

    packets = read_csv(args.filename)

    analyse_packet_types(packets)
    analyse_frequency_use(packets)
    analyse_unique_devices(packets)
    analyse(packets)

if __name__ == "__main__":
    main()
