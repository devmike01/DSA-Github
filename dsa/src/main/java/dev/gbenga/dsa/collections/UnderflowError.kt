package dev.gbenga.dsa.collections


class UnderflowError(message: String= "Empty queue cannot be dequeued"): Exception(message)