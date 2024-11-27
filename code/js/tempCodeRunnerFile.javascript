let value = 0
console.log("START")
setTimeout(() => {value = 5}, 1000)
setTimeout(() => {value = 2}, 3000)
setTimeout(() => { while(value!=2) console.log(`value=${value}. WAITING`)}, 2000)
console.log("DONE")
