document.addEventListener("DOMContentLoaded", () => {
  // Definir data padrão como hoje
  const hoje = new Date().toISOString().split("T")[0]
  document.getElementById("dataReserva").value = hoje

  // Submit do formulário
  document.getElementById("reservaForm").addEventListener("submit", criarReserva)
})

async function criarReserva(event) {
  event.preventDefault()

  const formData = new FormData(event.target)
  const reservaData = {
    clienteId: Number.parseInt(formData.get("clienteId")),
    livroId: Number.parseInt(formData.get("livroId")),
    dataReserva: formData.get("dataReserva"),
    status: formData.get("status"),
  }

  try {
    const response = await apiRequest(API_ENDPOINTS.reservas, {
      method: "POST",
      body: JSON.stringify(reservaData),
    })

    if (response.data) {
      showAlert("Reserva criada com sucesso!", "success")
      document.getElementById("reservaForm").reset()

      // Redefinir data padrão
      const hoje = new Date().toISOString().split("T")[0]
      document.getElementById("dataReserva").value = hoje
    } else if (response.error) {
      showAlert(response.error.message || "Erro ao criar reserva", "error")
    }
  } catch (error) {
    console.error("Erro ao criar reserva:", error)
    showAlert("Erro ao criar reserva", "error")
  }
}

async function listarTodasReservas() {
  try {
    const response = await apiRequest(API_ENDPOINTS.reservas)

    if (response.data) {
      exibirListaReservas(response.data, "Todas as Reservas")
      document.getElementById("resultadoBusca").style.display = "none"
    } else {
      showAlert("Nenhuma reserva encontrada", "info")
    }
  } catch (error) {
    console.error("Erro ao listar reservas:", error)
    showAlert("Erro ao carregar lista de reservas", "error")
  }
}

async function buscarReserva() {
  const id = document.getElementById("buscarReservaId").value

  if (!id) {
    showAlert("Digite o ID da reserva", "error")
    return
  }

  try {
    const response = await apiRequest(`${API_ENDPOINTS.reservas}/${id}`)

    if (response.data) {
      exibirDadosReserva(response.data)
      document.getElementById("listaReservas").style.display = "none"
    } else {
      showAlert("Reserva não encontrada", "error")
      document.getElementById("resultadoBusca").style.display = "none"
    }
  } catch (error) {
    console.error("Erro ao buscar reserva:", error)
    showAlert("Erro ao buscar reserva", "error")
    document.getElementById("resultadoBusca").style.display = "none"
  }
}

function exibirListaReservas(reservas, titulo) {
  const container = document.getElementById("listaReservas")
  const tabela = document.getElementById("tabelaReservas").getElementsByTagName("tbody")[0]

  // Limpar tabela
  tabela.innerHTML = ""

  // Atualizar título
  container.querySelector("h3").textContent = `📅 ${titulo}`

  // Preencher tabela
  reservas.forEach((reserva) => {
    const row = tabela.insertRow()
    const statusClass = getReservaStatusClass(reserva.status)

    row.innerHTML = `
            <td>${reserva.id}</td>
            <td>${reserva.clienteId}</td>
            <td>${reserva.livroId}</td>
            <td>${formatDate(reserva.dataReserva)}</td>
            <td><span class="status ${statusClass}">${reserva.status}</span></td>
            <td class="actions">
                <button class="btn btn-danger btn-sm" onclick="excluirReserva(${reserva.id})">Excluir</button>
            </td>
        `
  })

  container.style.display = "block"
}

function exibirDadosReserva(reserva) {
  const container = document.getElementById("dadosReserva")

  container.innerHTML = `
        <div class="reserva-info">
            <p><strong>ID:</strong> ${reserva.id}</p>
            <p><strong>Cliente ID:</strong> ${reserva.clienteId}</p>
            <p><strong>Livro ID:</strong> ${reserva.livroId}</p>
            <p><strong>Data da Reserva:</strong> ${formatDate(reserva.dataReserva)}</p>
            <p><strong>Status:</strong> <span class="status ${getReservaStatusClass(reserva.status)}">${reserva.status}</span></p>
            <div class="actions" style="margin-top: 1rem;">
                <button class="btn btn-danger btn-sm" onclick="excluirReserva(${reserva.id})">Excluir</button>
            </div>
        </div>
    `

  document.getElementById("resultadoBusca").style.display = "block"
}

async function excluirReserva(id) {
  if (!confirm("Tem certeza que deseja excluir esta reserva?")) {
    return
  }

  try {
    const response = await fetch(`${API_ENDPOINTS.reservas}/${id}`, {
      method: "DELETE",
    })

    if (response.ok) {
      showAlert("Reserva excluída com sucesso!", "success")

      // Atualizar lista se estiver visível
      if (document.getElementById("listaReservas").style.display !== "none") {
        listarTodasReservas()
      }

      // Limpar busca individual se for a mesma reserva
      if (document.getElementById("resultadoBusca").style.display !== "none") {
        document.getElementById("resultadoBusca").style.display = "none"
      }
    } else {
      showAlert("Erro ao excluir reserva", "error")
    }
  } catch (error) {
    console.error("Erro ao excluir reserva:", error)
    showAlert("Erro ao excluir reserva", "error")
  }
}

function getReservaStatusClass(status) {
  switch (status) {
    case "ATIVA":
      return "status-ativo"
    case "CANCELADA":
      return "status-cancelada"
    case "CONCLUIDA":
      return "status-concluida"
    default:
      return "status-ativo"
  }
}
