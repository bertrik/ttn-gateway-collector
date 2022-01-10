#!/usr/bin/env python3

"""
   Analyses a CSV file of LoRaWAN gateway traffic.
"""

import argparse
import csv

# the net ids of the major operators, others will be counted as 'Other'
netids = {  0x00 : 'Experimental',
            0x03 : 'Proximus',
            0x0A : 'KPN',
            0x13 : 'TheThingsNetwork',
            0x24 : 'Helium',
            0x62 : 'Operator_62'}

def get_operator(dev_addr):
    """takes a devaddr in hex and return the operator it belongs to """
    netid = int(dev_addr, 16) >> 25
    return netids.get(netid, 'Other');

def analyse(filename):
    """ Analyses a CSV file with the specified file name and prints the resuls to stdout """

    # group airtime and total packets by operator
    timedict = {}
    pktsdict = {}
    pkttypes = {}
    time_dev_total = 0
    pkts_dev_total = 0
    pkts_total = 0
    with open(filename, encoding="utf-8") as csvfile:
        reader = csv.DictReader(csvfile, delimiter=',', quotechar='"')
        for row in reader:
            dev_addr = row['dev_addr']
            if dev_addr:
                operator = get_operator(dev_addr)
                airtime = float(row['airtime'])
                timedict[operator] = timedict.get(operator, 0.0) + airtime
                pktsdict[operator] = pktsdict.get(operator, 0) + 1
                time_dev_total += airtime
                pkts_dev_total += 1
            pkt_type = row['type']
            pkttypes[pkt_type] = pkttypes.get(pkt_type, 0) + 1
            pkts_total += 1

    print(f'\nPacket types: ({pkts_total} total)')
    for pkt_type,value in sorted(pkttypes.items(), key=lambda item: item[1], reverse=True):
        print(f'{pkt_type:>20}: {value / pkts_total:>5.1%} = {value}')

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
    analyse(args.filename)

if __name__ == "__main__":
    main()
