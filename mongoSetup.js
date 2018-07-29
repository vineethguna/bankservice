let error = true

let res = [
  db.container.drop(),
  db.container.drop(),
  db.accounts.createIndex({ username: 1 }, { unique: true })
]

printjson(res)

if (error) {
  print('Error, exiting')
  quit(1)
}